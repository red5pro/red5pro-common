package com.red5pro.io.rtp;

/**
 * Output stream abstraction for an RTP connector.
 */
public interface IRTPConnectorOutputStream {
    /**
     * Writes data to the stream. Pulled from standard JMF class -- see <a href=
     * "http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/rtp/OutputDataStream.html"
     * target="_blank">this class in the JMF Javadoc</a>. Originally authored by Ken Larson.
     *
     * @param buffer the data to write
     * @param offset the start offset in the buffer
     * @param length the number of bytes to write
     * @param type the type of data being written
     * @return the number of bytes written
     */
    int write(byte[] buffer, int offset, int length, int type);
}
