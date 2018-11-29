package com.red5pro.cluster.streams;

/**
 * Defines an RTMP path for publishing clients.
 * Port is assumed to be rtmp for RTC and RTSP clients when resolving instances.
 * 
 * @author Andy Shaules
 */
public class Ingest {
    /**
     * Only one version of the stream available.
     */
    public static final int SBR = 0;

    /**
     * Highest quality of multi bitrate stream variations. Lower quality streams are higher indexes. 
     */
    public static final int MBR_MAIN = 1;

    private final String host;

    private final int port;

    private final String context;

    private final String name;

    private final int level;

    private Ingest(String host, int port) {
        this.host = host;
        this.port = port;
        this.context = null;
        this.name = null;
        this.level = 0;
    }

    private Ingest(String host, int port, String context, String name, int level) {
        this.host = host;
        this.port = port;
        this.context = context;
        this.name = name;
        this.level = level;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public boolean isMBR() {
        return level > SBR;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + level;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ingest other = (Ingest) obj;
        if (context == null) {
            if (other.context != null)
                return false;
        } else if (!context.equals(other.context))
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (level != other.level)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Ingest [host=" + host + ", port=" + port + ", context=" + context + ", name=" + name + ", level=" + level + "]";
    }
    
    public static Ingest build(String host, int port) {
        return new Ingest(host, port);
    }
    
    public static Ingest build(String host, int port, String context, String name, int level) {
        return new Ingest(host, port, context, name, level);
    }

}
