package com.red5pro.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
import com.red5pro.ice.AbstractResponseCollector;
import com.red5pro.ice.BaseStunMessageEvent;
import com.red5pro.ice.ResponseCollector;
import com.red5pro.ice.StunFailureEvent;
import com.red5pro.ice.StunResponseEvent;
import com.red5pro.ice.StunTimeoutEvent;
import com.red5pro.ice.Transport;
import com.red5pro.ice.TransportAddress;
import com.red5pro.ice.attribute.Attribute;
import com.red5pro.ice.attribute.MappedAddressAttribute;
import com.red5pro.ice.attribute.XorMappedAddressAttribute;
import com.red5pro.ice.nio.IceHandler;
import com.red5pro.ice.nio.IceTransport;
import com.red5pro.ice.message.Message;
import com.red5pro.ice.message.MessageFactory;
import com.red5pro.ice.message.Request;
import com.red5pro.ice.message.Response;
import com.red5pro.ice.socket.IceSocketWrapper;
import com.red5pro.ice.stack.StunStack;
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

    // default usable addresses
    private static final String[] DEFAULT_USABLE_ADDRESSES = new String[] { "0.0.0.0", "::", "0000:0000:0000:0000:0000:0000:0000:0000" };

    // IP check service
    // Two options: https://checkip.amazonaws.com or https://icanhazip.com
    private static final String IP_CHECK_URI = System.getProperty("ip.check.uri", "https://checkip.amazonaws.com");

    // represents an un-set IP address
    private static final String NO_IP_ADDRESS = "no-ip";

    // public / external-facing IP address
    private static final AtomicReference<String> serverIp = new AtomicReference<>(NO_IP_ADDRESS);

    // public / external-facing IPv6 address
    private static final AtomicReference<String> serverIpV6 = new AtomicReference<>(NO_IP_ADDRESS);

    // private / local IP address
    private static final AtomicReference<String> serverLocalIp = new AtomicReference<>(NO_IP_ADDRESS);

    // private / local IPv6 address
    private static final AtomicReference<String> serverLocalIpV6 = new AtomicReference<>(NO_IP_ADDRESS);

    // list of usable addresses
    private static final Set<String> usableAddresses = new HashSet<>();

    // collection of end-points created herein with the key being constructed based on type and address+port combo
    private static ConcurrentMap<String, IngestEndpoint<?>> ingestEndPoints = new ConcurrentHashMap<>();

    // network properties
    private static Properties props = new Properties();

    // default transport for stun requests  ("udp" or "tcp")
    private static String defaultTransport = "udp";

    // default stun server address
    private static String defaultStunAddress;

    // whether or not IPv6 is enabled via props for ICE
    private static boolean iceIPv6Enabled;

    // network topology in-use
    private static TopologyMode topologyMode = TopologyMode.DEFAULT;

    // dns port
    private static int dnsPort = 53;

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
                return resolveIPOverHTTP(IP_CHECK_URI);
            }

            String getLocalAddress() {
                String ipAddress = null;
                try {
                    // cheap and dirty way to get the preferred local IP
                    DatagramSocket socket = null;
                    try {
                        socket = new DatagramSocket();
                        // hits the google dns server
                        socket.connect(InetAddress.getByName("8.8.8.8"), dnsPort);
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
                    }
                    log.debug("Local address (detected): {}", ipAddress);
                } catch (Exception e) {
                    log.warn("Exception getting local address", e);
                }
                return ipAddress;
            }

            String getPublicIPV6() {
                String ipAddress = null;
                try {
                    // cheap and dirty way to get a public IPv6 address
                    DatagramSocket socket = null;
                    try {
                        socket = new DatagramSocket();
                        // hits the google ipv6 dns server
                        socket.connect(InetAddress.getByName("2001:4860:4860:0:0:0:0:8888"), dnsPort);
                        ipAddress = socket.getLocalAddress().getHostAddress();
                    } catch (Throwable t) {
                        log.warn("Exception getting public address via dgram", t);
                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (Exception ce) {
                            }
                        }
                    }
                    log.debug("Public address (detected): {}", ipAddress);
                } catch (Exception e) {
                    log.warn("Exception getting public address", e);
                }
                return ipAddress;
            }

            String getLocalAddressV6() {
                return getLocalInterfaceIPV6();
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
                    ipAddress = resolveIPOverHTTP(IP_CHECK_URI);
                }
                return ipAddress;
            }

            String getLocalAddress() {
                return resolveIPOverHTTP("http://169.254.169.254/latest/meta-data/local-ipv4");
            }

            String getPublicIPV6() {
                String ipAddress = resolveIPOverHTTP("http://fe80::a9fe:a9fe/latest/meta-data/public-ipv6");
                // handle the wavelength case where public-ipv4 returns nothing
                if (ipAddress == null) {
                    ipAddress = resolveIPOverHTTP(IP_CHECK_URI);
                }
                return ipAddress;
            }

            String getLocalAddressV6() {
                return resolveIPOverHTTP("http://fe80::a9fe:a9fe/latest/meta-data/local-ipv6");
            }

        };

        String getPublicIP() {
            return NO_IP_ADDRESS;
        }

        String getLocalAddress() {
            return NO_IP_ADDRESS;
        }

        String getPublicIPV6() {
            return NO_IP_ADDRESS;
        }

        String getLocalAddressV6() {
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
            PortManager.setAllowSystemPorts(Boolean.valueOf(props.getProperty("allow.sys.ports", "false")));
            // timeout to use when checking port availability
            PortManager.setSoTimeout(Integer.valueOf(props.getProperty("check.port.availability.timeout", "5")));
            log.debug("Port range: {}", PortManager.getRange());
            // set local properties
            defaultTransport = props.getProperty("ice.default.transport", "udp");
            defaultStunAddress = props.getProperty("stun.address", "stun.l.google.com:19302");
            // set/get ice4j props
            System.setProperty("com.red5pro.ice.BIND_RETRIES", "1");
            System.setProperty("com.red5pro.ice.ice.harvest.NAT_HARVESTER_DEFAULT_TRANSPORT", defaultTransport);
            System.setProperty("com.red5pro.ice.TERMINATION_DELAY", props.getProperty("ice.termination.delay", "500"));
            // whether or not IPv6 is enabled
            iceIPv6Enabled = Boolean.valueOf(props.getProperty("ice.enable.ipv6", "false"));
        } catch (IOException e) {
            log.warn("Exception reading properties", e);
        } finally {
            // add to the usable addresses
            for (String addr : DEFAULT_USABLE_ADDRESSES) {
                usableAddresses.add(addr);
            }
        }
    }

    /**
     * Resets the network IP addresses.
     */
    public static void resetIPAddresses() {
        usableAddresses.remove(serverIp.get());
        serverIp.set(NO_IP_ADDRESS);
        usableAddresses.remove(serverLocalIp.get());
        serverLocalIp.set(NO_IP_ADDRESS);
        usableAddresses.remove(serverIpV6.get());
        serverIpV6.set(NO_IP_ADDRESS);
        usableAddresses.remove(serverLocalIpV6.get());
        serverLocalIpV6.set(NO_IP_ADDRESS);
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
            if (ipAddress != null) {
                // ipv6
                if (ipAddress.contains(":")) {
                    setServerIpV6(ipAddress);
                } else {
                    String ipV6Address = topologyMode.getPublicIPV6();
                    if (ipV6Address.contains(":")) {
                        setServerIpV6(ipV6Address);
                    }
                }
            }
        }
        log.debug("Public address (stored): {}", ipAddress);
        // one last check to ensure we send null for our no-ip placeholder
        ipAddress = NO_IP_ADDRESS.equals(ipAddress) ? null : ipAddress;
        log.debug("Public address: {}", ipAddress);
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
            if (ipAddress != null && ipAddress.contains(":")) {
                setServerLocalIpV6(ipAddress);
            } else {
                String ipV6Address = topologyMode.getLocalAddressV6();
                if (ipV6Address.contains(":")) {
                    setServerLocalIpV6(ipV6Address);
                }
            }
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
                            if (ipAddress != null) {
                                // ipv6
                                if (ipAddress.contains(":")) {
                                    setServerLocalIpV6(ipAddress);
                                } else {
                                    setServerLocalIp(ipAddress);
                                }
                            }
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
        log.debug("Local address: {}", ipAddress);
        return ipAddress;
    }

    public static String getPublicAddressV6() {
        return serverIpV6.get();
    }

    public static String getLocalAddressV6() {
        return serverLocalIpV6.get();
    }

    /**
     * Returns if the public address is IPv6.
     *
     * @return true if IPv6, false if IPv4
     */
    public static boolean isPublicAddressV6() {
        String ipAddress = serverIp.get();
        return NO_IP_ADDRESS.equals(ipAddress) ? false : ipAddress.contains(":");
    }

    public static boolean isLocalAddressV6() {
        String ipAddress = serverLocalIp.get();
        return NO_IP_ADDRESS.equals(ipAddress) ? false : ipAddress.contains(":");
    }

    public static boolean hasPublicAddressV6() {
        return !NO_IP_ADDRESS.equals(serverIpV6.get());
    }

    public static boolean hasLocalAddressV6() {
        return !NO_IP_ADDRESS.equals(serverLocalIpV6.get());
    }

    public static void setServerIp(String ipAddress) {
        log.debug("setServerIp: {}", ipAddress);
        // disallow null or blank IP
        if (ipAddress != null && ipAddress.length() > 6) {
            NetworkManager.serverIp.set(ipAddress);
            usableAddresses.add(ipAddress);
        }
    }

    public static void setServerLocalIp(String ipAddress) {
        log.debug("setServerLocalIp: {}", ipAddress);
        // disallow null or blank IP
        if (ipAddress != null && ipAddress.length() > 6) {
            NetworkManager.serverLocalIp.set(ipAddress);
            usableAddresses.add(ipAddress);
        }
    }

    public static void setServerIpV6(String ipAddress) {
        log.debug("setServerIpV6: {}", ipAddress);
        // disallow null or blank IP
        if (ipAddress != null && ipAddress.length() > 6) {
            NetworkManager.serverIpV6.set(ipAddress);
            usableAddresses.add(ipAddress);
        }
    }

    public static void setServerLocalIpV6(String ipAddress) {
        log.debug("setServerLocalIpV6: {}", ipAddress);
        // disallow null or blank IP
        if (ipAddress != null && ipAddress.length() > 6) {
            NetworkManager.serverLocalIpV6.set(ipAddress);
            usableAddresses.add(ipAddress);
        }
    }

    private static boolean usableAddress(String ipAddress) {
        return usableAddresses.contains(ipAddress);
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
        // if its the ANY address accept it by default
        if (usableAddress(ipAddress)) {
            // if the address is local or public then its a-ok
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
        serverIpV6.set(NO_IP_ADDRESS);
        serverLocalIpV6.set(NO_IP_ADDRESS);
        usableAddresses.clear();
        // add to the usable addresses
        for (String addr : DEFAULT_USABLE_ADDRESSES) {
            usableAddresses.add(addr);
        }
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
        } catch (FileNotFoundException fnfe) {
            // this will occur in a wavelength zone where carrier IP is enabled
            if (log.isDebugEnabled()) {
                log.warn("Host could not be reached, probably carrier IP enabled zone", fnfe);
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
        log.debug("Public IP: {}", publicIP);
        return publicIP;
    }

    /**
     * Returns the local interface IPv6 address, if bound.
     *
     * @return local network IPv6 address if located and bound, otherwise no-ip
     */
    public static String getLocalInterfaceIPV6() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface interface1 = interfaces.nextElement();
                Enumeration<InetAddress> addresses = interface1.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet6Address && !address.isLoopbackAddress()) {
                        // Check if the IPv6 address is in the private address range
                        String ipAddress = address.getHostAddress();
                        log.debug("Local address (nic): {}", ipAddress);
                        if (ipAddress.startsWith("fe80::") || ipAddress.startsWith("fe80:0:") || ipAddress.startsWith("fc00::") || ipAddress.startsWith("fec0::")) {
                            // chop the interface off the end if its there
                            int index = ipAddress.indexOf("%");
                            if (index > 0) {
                                ipAddress = ipAddress.substring(0, index);
                            }
                            return ipAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.warn("Exception getting local interface", e);
        }
        return NO_IP_ADDRESS;
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

    public static boolean isIceIPv6Enabled() {
        return iceIPv6Enabled;
    }

    public static void setIceIPv6Enabled(boolean iceIPv6Enabled) {
        NetworkManager.iceIPv6Enabled = iceIPv6Enabled;
    }

}
