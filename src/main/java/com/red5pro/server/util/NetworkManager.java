package com.red5pro.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.ice4j.AbstractResponseCollector;
import org.ice4j.BaseStunMessageEvent;
import org.ice4j.ResponseCollector;
import org.ice4j.StunFailureEvent;
import org.ice4j.StunResponseEvent;
import org.ice4j.StunTimeoutEvent;
import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.attribute.Attribute;
import org.ice4j.attribute.MappedAddressAttribute;
import org.ice4j.attribute.XorMappedAddressAttribute;
import org.ice4j.ice.nio.IceHandler;
import org.ice4j.ice.nio.IceTransport;
import org.ice4j.message.Message;
import org.ice4j.message.MessageFactory;
import org.ice4j.message.Request;
import org.ice4j.message.Response;
import org.ice4j.socket.IceSocketWrapper;
import org.ice4j.stack.StunStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.SourceType;

/**
 * Provides network functions.
 * 
 * @author Paul Gregoire
 */
public class NetworkManager {

    private static Logger log = LoggerFactory.getLogger(NetworkManager.class);

    // static Amazon IP service
    private static final String AWS_IP_CHECK_URI = "http://checkip.amazonaws.com";

    // represents an un-set IP address
    private static final String NO_IP_ADDRESS = "no-ip";

    // public / external-facing IP address
    private static final AtomicReference<String> serverIp = new AtomicReference<>(NO_IP_ADDRESS);

    // private / local IP address
    private static final AtomicReference<String> serverLocalIp = new AtomicReference<>(NO_IP_ADDRESS);

    // collection of end-points created herein with the key being constructed based on type and address+port combo
    private static ConcurrentMap<String, IngestEndpoint<?>> ingestEndPoints = new ConcurrentHashMap<>();

    // network properties
    private static Properties props = new Properties();

    // default transport for stun requests  ("udp" or "tcp")
    private static String defaultTransport = "udp";

    // default stun server address
    private static String defaultStunAddress;

    // network topology in-use
    private static TopologyMode topologyMode = TopologyMode.DEFAULT;

    /**
     * Network type.
     */
    public enum NetworkType {
        IN; // internet
    }

    /**
     * Network address type.
     */
    public enum AddressType {
        IP4, // IPv4
        IP6; // IPv6
    }

    /**
     * Topology in-use.
     */
    public enum TopologyMode {

        DEFAULT // uses aws service to find network properties
        {

            String getPublicIP() {
                return resolveIPOverHTTP(AWS_IP_CHECK_URI);
            }

            String getLocalAddress() {
                String ipAddress = null;
                try {
                    int port = PortManager.findFreeUdpPort();
                    // cheap and dirty way to get the preferred local IP
                    DatagramSocket socket = null;
                    try {
                        socket = new DatagramSocket();
                        socket.connect(InetAddress.getByName("8.8.8.8"), port);
                        ipAddress = socket.getLocalAddress().getHostAddress();
                    } catch (Throwable t) {
                        log.warn("Exception getting local address via dgram", t);
                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (Exception ce) {
                            }
                        }
                        PortManager.clearRTPServerPort(port);
                    }
                    log.debug("Local address (detected): {}", ipAddress);
                } catch (Exception e) {
                    log.warn("Exception getting local address", e);
                }
                return ipAddress;
            }

        },
        PROPERTIES // configured via properties system then file
        {

            String getPublicIP() {
                String ipAddress = System.getenv("PUBLIC_IP");
                if (StringUtils.isBlank(ipAddress)) {
                    // read from java params 
                    ipAddress = System.getProperty("public.ip");
                    if (StringUtils.isBlank(ipAddress)) {
                        ipAddress = props.getProperty("force.public.ip");
                    }
                }
                return ipAddress;
            }

            String getLocalAddress() {
                String ipAddress = System.getenv("LOCAL_IP");
                if (StringUtils.isBlank(ipAddress)) {
                    ipAddress = System.getProperty("local.ip");
                    if (StringUtils.isBlank(ipAddress)) {
                        ipAddress = props.getProperty("force.local.ip");
                    }
                }
                return ipAddress;
            }

        },
        AWS // uses aws services
        {

            String getPublicIP() {
                String ipAddress = resolveIPOverHTTP("http://169.254.169.254/latest/meta-data/public-ipv4");
                // handle the wavelength case where public-ipv4 returns nothing
                if (ipAddress == null) {
                    resolveIPOverHTTP(AWS_IP_CHECK_URI);
                }
                return ipAddress;
            }

            String getLocalAddress() {
                return resolveIPOverHTTP("http://169.254.169.254/latest/meta-data/local-ipv4");
            }

        };

        String getPublicIP() {
            return NO_IP_ADDRESS;
        }

        String getLocalAddress() {
            return NO_IP_ADDRESS;
        }

    }

    static {
        // load up the network properties
        try (InputStream input = new FileInputStream(System.getProperty("red5.config_root") + File.separatorChar + "network.properties")) {
            // load properties
            props.load(input);
            // configure port range
            PortManager.setRtpPortBase(Integer.valueOf(props.getProperty("port.min", "49152")));
            PortManager.setRtpPortCeiling(Integer.valueOf(props.getProperty("port.max", "65535")));
            log.info("Port range: {}", PortManager.getRange());
            // set local properties
            defaultTransport = props.getProperty("ice.default.transport", "udp");
            defaultStunAddress = props.getProperty("stun.address", "stun.l.google.com:19302");
            // set ice4j props
            System.setProperty("org.ice4j.ice.harvest.NAT_HARVESTER_DEFAULT_TRANSPORT", defaultTransport);
        } catch (IOException e) {
            log.warn("Exception reading properties", e);
        }
    }

    /**
     * Returns a publicly accessible IP address for this originator using a free
     * service on Amazon AWS.
     * 
     * @return IP address
     */
    public static String getPublicAddress() {
        String ipAddress = serverIp.get();
        if (NO_IP_ADDRESS.equals(ipAddress)) {
            // gets and returns the public address based on topology
            ipAddress = topologyMode.getPublicIP();
            // if the address lookup failed, try via stun using ice4j to get our public IP
            if (NO_IP_ADDRESS.equals(ipAddress)) {
                String localIP = topologyMode.getLocalAddress();
                if (NO_IP_ADDRESS.equals(localIP)) {
                    // cannot hit stun server with invalid / unresolved IP
                    log.info("Local IP is not set, cannot query stun");
                } else {
                    try {
                        Transport transport = Transport.parse(defaultTransport);
                        String[] stun = defaultStunAddress.split(":");
                        TransportAddress stunTransportAddress = new TransportAddress(stun[0], Integer.valueOf(stun[1]), transport);
                        TransportAddress localTransportAddress = new TransportAddress(localIP, PortManager.findFreeUdpPort(), transport);
                        ipAddress = resolvePublicIP(localTransportAddress, stunTransportAddress);
                    } catch (Throwable t) {
                        log.warn("Exception contacting STUN server", t);
                    }
                }
            }
            // store it
            setServerIp(ipAddress);
        }
        log.debug("Public address (stored): {}", ipAddress);
        // one last check to ensure we send null for our no-ip placeholder
        ipAddress = NO_IP_ADDRESS.equals(ipAddress) ? null : ipAddress;
        log.info("Public address: {}", ipAddress);
        return ipAddress;
    }

    /**
     * Returns the private / local IP address for the active network interface(s).
     * 
     * @return IP address
     */
    public static String getLocalAddress() {
        String ipAddress = serverLocalIp.get();
        if (NO_IP_ADDRESS.equals(ipAddress)) {
            // gets and returns the local address based on topology
            ipAddress = topologyMode.getLocalAddress();
            // store it
            setServerLocalIp(ipAddress);
        }
        // check for null address before continuing
        BUST_OUT: // check nic cards
        if (ipAddress == null) {
            try {
                Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
                while (ifaces.hasMoreElements()) {
                    NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                    Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
                    while (iaddresses.hasMoreElements()) {
                        InetAddress iaddress = (InetAddress) iaddresses.nextElement();
                        if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress() && !iaddress.isSiteLocalAddress()) {
                            ipAddress = iaddress.getHostAddress() != null ? iaddress.getHostAddress() : iaddress.getHostName();
                            log.debug("Local address (nic): {}", ipAddress);
                            // store it
                            setServerLocalIp(ipAddress);
                            break BUST_OUT;
                        }
                    }
                }
            } catch (SocketException e) {
                log.warn("Exception determining nic address", e);
            }
        }
        // last resort, local host
        if (ipAddress == null) {
            try {
                ipAddress = InetAddress.getLocalHost().getHostAddress() != null ? InetAddress.getLocalHost().getHostAddress() : InetAddress.getLocalHost().getHostName();
                // store it
                setServerLocalIp(ipAddress);
            } catch (UnknownHostException e) {
                log.warn("Exception determining local address", e);
            }
        }
        log.debug("Local address (stored): {}", ipAddress);
        // one last check to ensure we send null for our no-ip placeholder
        ipAddress = NO_IP_ADDRESS.equals(ipAddress) ? null : ipAddress;
        log.info("Local address: {}", ipAddress);
        return ipAddress;
    }

    public static void setServerIp(String ipAddress) {
        log.debug("setServerIp: {}", ipAddress);
        // disallow null or blank IP
        if (ipAddress != null && ipAddress.length() > 6) {
            NetworkManager.serverIp.set(ipAddress);
        }
    }

    public static void setServerLocalIp(String ipAddress) {
        log.debug("setServerLocalIp: {}", ipAddress);
        // disallow null or blank IP
        if (ipAddress != null && ipAddress.length() > 6) {
            NetworkManager.serverLocalIp.set(ipAddress);
        }
    }

    /**
     * Validates a given IP address and port (optional) for binding.
     * 
     * @param ipAddress
     * @param port
     * @return true if bindable and false otherwise
     */
    public static boolean validIPAddress(String ipAddress, int port) {
        log.debug("validIPAddress: {}:{}", ipAddress, port);
        // start off invalid
        boolean valid = false;
        // check the port
        boolean validPort = (port > 0 && port < 65535);
        // if its the ANY address accept it
        if ("0.0.0.0".equals(ipAddress)) {
            valid = true;
        } else if (getLocalAddress().equals(ipAddress) || getPublicAddress().equals(ipAddress)) {
            // if the address is equal to our local or public IP address then its a-ok so
            // far
            log.debug("Local or public address matched");
            // IP is valid since we're already bound to it
            valid = true;
        } else {
            InetAddress addr;
            try {
                // attempt to create an inet address
                addr = InetAddress.getByName(ipAddress);
                // check for Class-D address and accept by default
                if (addr.isMulticastAddress()) {
                    log.debug("Multicast address valid");
                    valid = true;
                } else {
                    int count = 0;
                    Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
                    while (ifaces.hasMoreElements()) {
                        NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                        Enumeration<InetAddress> iaddresses = iface.getInetAddresses();
                        while (iaddresses.hasMoreElements()) {
                            InetAddress iaddress = (InetAddress) iaddresses.nextElement();
                            log.debug("Local address {}: {} link local: {} site local: {}", iface.getName(), iaddress, iaddress.isLinkLocalAddress(), iaddress.isSiteLocalAddress());
                            if (!iaddress.isLoopbackAddress() && !iaddress.isLinkLocalAddress()) {
                                String nicAddress = iaddress.getHostAddress() != null ? iaddress.getHostAddress() : iaddress.getHostName();
                                // log.debug("Local address {}: {}", iface.getName(), nicAddress);
                                if (ipAddress.equals(nicAddress)) {
                                    return true & validPort;
                                }
                            }
                        }
                        count++;
                    }
                    log.debug("Network interfaces: {}", count);
                }
            } catch (Exception e) {
                log.warn("Exception validating address {}", ipAddress, e);
            }
        }
        return valid & validPort;
    }

    /**
     * Validates a given IP address and port (optional) for binding.
     * 
     * @param ipAddress
     * @param port
     * @return true if bindable and false otherwise
     */
    public static boolean validMulticastIPAddress(String groupName, int groupPort) {
        log.debug("validMulticastIPAddress: {}:{}", groupName, groupPort);
        boolean valid = false;
        // start off by checking the port
        boolean validPort = (groupPort > 0 && groupPort < 65535);
        InetAddress addr;
        try {
            // attempt to create an inet address
            addr = InetAddress.getByName(groupName);
            // check for Class-D address and accept by default
            if (addr.isMulticastAddress()) {
                valid = true;
            }
        } catch (Exception e) {
            log.warn("Exception validating address {}", groupName, e);
        }
        return valid & validPort;
    }

    /**
     * Returns the ingest end-point map.
     * 
     * @return ingestEndPoints
     */
    public static ConcurrentMap<String, IngestEndpoint<?>> getIngestEndPoints() {
        return ingestEndPoints;
    }

    /**
     * Returns an unmodifiable copy of the ingest end-points matching the source
     * type.
     * 
     * @return ingestEndPoints
     */
    public static Map<String, IngestEndpoint<?>> getIngestEndPointsByType(SourceType type) {
        // create a non-concurrent copy of the current end-points map
        Map<String, IngestEndpoint<?>> result = ingestEndPoints.entrySet().stream().filter(e -> e.getValue().getType() == type).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // make it unmodifiable / read-only
        return (Map<String, IngestEndpoint<?>>) Collections.unmodifiableMap(result);
    }

    /**
     * Removes an end-point by its id.
     * 
     * @param endPointId
     * @return ingest end-point matching the id or null if no match exists
     */
    public static IngestEndpoint<?> disposeIngestEndpoint(String endPointId) {
        IngestEndpoint<?> endPoint = ingestEndPoints.remove(endPointId);
        log.debug("disposeIngestEndpoint: {} {}", endPointId, endPoint);
        if (endPoint != null) {
            endPoint.getConnection().close();
        } else {
            log.warn("Endpoint not found, most likely already disposed {}", endPointId);
        }
        return endPoint;
    }

    /**
     * Resets the atomic references.
     */
    public static void reset() {
        topologyMode = TopologyMode.DEFAULT;
        serverIp.set(NO_IP_ADDRESS);
        serverLocalIp.set(NO_IP_ADDRESS);
    }

    /**
     * Resolves an IP with a given URL.
     * 
     * @param url location of IP resolver service
     * @return IP address or null if some failure occurs
     */
    private static String resolveIPOverHTTP(String url) {
        String ipAddress = null;
        BufferedReader in = null;
        try {
            URL checkip = new URL(url);
            URLConnection con = checkip.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            // read the first line and ensure that it starts with a number, if not we're most likely getting html
            String line = in.readLine();
            if (NumberUtils.isParsable(line.charAt(0) + "")) {
                ipAddress = line.trim();
                log.debug("Public address (detected): {}", ipAddress);
            } else {
                log.warn("Service returned unusable results: {}", line);
            }
        } catch (Throwable t) {
            log.warn("Host could not be reached or timed-out", t);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("Exception getting public IP", e);
                }
            }
        }
        return ipAddress;
    }

    /**
     * Resolves the servers public IP address using a STUN binding request.
     *
     * @param localTransportAddress
     * @param stunTransportAddress
     * @return public IP address or null if some failure occurs
     * @throws IOException
     * @throws InterruptedException
     */
    private static String resolvePublicIP(TransportAddress localTransportAddress, TransportAddress stunTransportAddress) throws IOException, InterruptedException {
        String publicIP = null;
        SynchronousQueue<Response> que = new SynchronousQueue<>();
        // collector for responses
        ResponseCollector responseCollector = new AbstractResponseCollector() {

            /**
             * Notifies this ResponseCollector that a transaction described by the specified BaseStunMessageEvent has failed. The possible
             * reasons for the failure include timeouts, unreachable destination, etc.
             *
             * @param event the BaseStunMessageEvent which describes the failed transaction and the runtime type of which specifies the failure reason
             * @see AbstractResponseCollector#processFailure(BaseStunMessageEvent)
             */
            @Override
            protected void processFailure(BaseStunMessageEvent event) {
                String msg;
                if (event instanceof StunFailureEvent) {
                    msg = "Unreachable";
                } else if (event instanceof StunTimeoutEvent) {
                    msg = "Timeout";
                } else {
                    msg = "Failure";
                }
                log.debug("ResponseCollector: {}", msg);
            }

            /**
             * Queues the received response.
             *
             * @param response a StunMessageEvent which describes the received STUN Response
             */
            @Override
            public void processResponse(StunResponseEvent response) {
                que.offer((Response) response.getMessage());
            }

        };
        // init the stun stack
        StunStack stunStack = new StunStack();
        // create an ice socket wrapper with the transport based on the addresses
        // supplied
        IceSocketWrapper iceSocket = IceSocketWrapper.build(localTransportAddress, stunTransportAddress);
        // add the wrapper to the stack
        if (iceSocket.isUDP()) {
            // when its udp, bind so we'll be listening
            stunStack.addSocket(iceSocket, stunTransportAddress, true);
        } else if (iceSocket.isTCP()) {
            // get the handler
            IceHandler handler = IceTransport.getIceHandler();
            // now connect as a client
            NioSocketConnector connector = new NioSocketConnector(1);
            SocketSessionConfig config = connector.getSessionConfig();
            config.setReuseAddress(true);
            config.setTcpNoDelay(true);
            // set an idle time of 30s (default)
            config.setIdleTime(IdleStatus.BOTH_IDLE, IceTransport.getTimeout());
            // set connection timeout of x milliseconds
            connector.setConnectTimeoutMillis(3000L);
            // add the ice protocol encoder/decoder
            connector.getFilterChain().addLast("protocol", IceTransport.getProtocolcodecfilter());
            // set the handler on the connector
            connector.setHandler(handler);
            // register
            handler.registerStackAndSocket(stunStack, iceSocket);
            // dont bind when using tcp, since java doesn't allow client+server at the same
            // time
            stunStack.addSocket(iceSocket, stunTransportAddress, false);
            // connect
            connector.setDefaultRemoteAddress(stunTransportAddress);
            ConnectFuture future = connector.connect(stunTransportAddress, localTransportAddress);
            future.addListener(new IoFutureListener<ConnectFuture>() {

                @Override
                public void operationComplete(ConnectFuture future) {
                    log.debug("operationComplete {} {}", future.isDone(), future.isCanceled());
                    if (future.isConnected()) {
                        IoSession sess = future.getSession();
                        if (sess != null) {
                            iceSocket.setSession(sess);
                        }
                    } else {
                        log.warn("Exception connecting", future.getException());
                    }
                }

            });
            future.awaitUninterruptibly();
        }
        Request bindingRequest = MessageFactory.createBindingRequest();
        stunStack.sendRequest(bindingRequest, stunTransportAddress, localTransportAddress, responseCollector);
        // wait for its arrival with a timeout of 3s
        Response res = que.poll(3000L, TimeUnit.MILLISECONDS);
        if (res != null) {
            // in classic STUN, the response contains a MAPPED-ADDRESS
            MappedAddressAttribute maAtt = (MappedAddressAttribute) res.getAttribute(Attribute.Type.MAPPED_ADDRESS);
            if (maAtt != null) {
                publicIP = maAtt.getAddress().getHostString();
            }
            // in STUN bis, the response contains a XOR-MAPPED-ADDRESS
            XorMappedAddressAttribute xorAtt = (XorMappedAddressAttribute) res.getAttribute(Attribute.Type.XOR_MAPPED_ADDRESS);
            if (xorAtt != null) {
                byte xoring[] = new byte[16];
                System.arraycopy(Message.MAGIC_COOKIE, 0, xoring, 0, 4);
                System.arraycopy(res.getTransactionID(), 0, xoring, 4, 12);
                publicIP = xorAtt.applyXor(xoring).getHostString();
            }
        }
        // clean up
        if (iceSocket != null) {
            iceSocket.close();
        }
        stunStack.shutDown();
        log.info("Public IP: {}", publicIP);
        return publicIP;
    }

    public static TopologyMode getTopologyMode() {
        return topologyMode;
    }

    public static void setTopologyMode(TopologyMode topologyMode) {
        NetworkManager.topologyMode = topologyMode;
    }

    public static void setTopologyMode(String topologyMode) {
        if (StringUtils.isNotBlank(topologyMode)) {
            NetworkManager.topologyMode = TopologyMode.valueOf(topologyMode.toUpperCase());
        }
    }

    public static String getDefaultTransport() {
        return defaultTransport;
    }

    public static String getDefaultStunAddress() {
        return defaultStunAddress;
    }

}
