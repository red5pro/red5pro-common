package com.red5pro.io.rtp;

public interface IRTPConnectorOutputStream {
    /**
     * Pulled from standard JMF class -- see <a href=
     * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/OutputDataStream.html"
     * target="_blank">this class in the JMF Javadoc</a>
     *
     * @author Ken Larson
     */
    int write(byte[] buffer, int offset, int length, int type);
}
