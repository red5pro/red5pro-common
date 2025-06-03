package com.red5pro.io.klv;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class for registering KLV codec dictionaries.
 * Using a red5 plugin, Spring bean, or reflection, call static method 'addKLVDecoder' at server start up.
 * Reflection: 
 * <pre>
 *  Class<?> clazz = Class.forName("com.red5pro.io.klv.KLVTagDecoder");
 *  Method m = clazz.getMethod("addKLVDecoder", String.class,Class.class); 
 *  m.invoke(clazz, "060e2b34020b01010e01030101000000", SMPTE0601.class);
 * </pre><p>
 * Red5Plugin:
 * <pre>
 *  public void doStart(){ * 
 *      KLVTagDecoder.addKLVDecoder("060e2b34020b01010e01030101000000", SMPTE0601.class);
 *  }
 * </pre><p>
 * Spring bean
 * <pre>{@code
 *  <!-- In red5-web.xml or other xml webcontext file, bean init-method references class method 'initialize()' 
 *      where it adds itself to registry. --> 
 *  <bean id="klvDecoder" class="com.red5pro.server.klv.SMPTE0601" init-method="initialize">
 *  </bean
 *  }
 *  
 *  ...
 *  
 *  public void initialize(){
 *      KLVTagDecoder.addKLVDecoder("060e2b34020b01010e01030101000000", SMPTE0601.class);
 *  }
 * </pre>
 * @author Andy
 * 
 */
public abstract class KLVTagDecoder {
    
    private static Map<String, Class<? extends KLVTagDecoder>> libraries = new HashMap<>();

    private static Map<String, KLVTagDecoder> cache = new HashMap<>();

    protected final static Logger logger = LoggerFactory.getLogger(KLVTagDecoder.class);
    /**
     * Register a class for decoding KLV streams from mpegts, zixi, or SRT input streams. Call at server startup. 
     * @param universalLabel
     * @param decoder
     */
    public static void addKLVDecoder(String universalLabel, Class<? extends KLVTagDecoder> decoder) {
    	logger.info("Adding KLV decoder: {} handler: {} ", universalLabel,decoder.getSimpleName());
    	libraries.put(universalLabel, decoder);
    }

    public static KLVTagDecoder getDecoder(String label) {
        if (cache.containsKey(label)) {
            return cache.get(label);
        }
        if (libraries.containsKey(label)) {
            try {
                logger.info("KLV decoder found.  UL: {}", label);
                KLVTagDecoder decoder = (KLVTagDecoder) libraries.get(label).getDeclaredConstructor().newInstance();
                cache.put(label, decoder);
                return decoder;
            } catch (Throwable e) {
            	logger.error("Failed to instantiate KLV decoder for label: {}", label, e);
            }
        }
        return null;
    }

    /**
     * Returns the universal label for the decoder's dictionary of tag key/value pairs.
     * @return
     */
    public abstract String getUniversalLabel();
    /**
     * Returns the friendly name for a tag id.
     * Example:
     * <pre> 
     * private static String[] tagNames = new String[] { "", "checksum", "unix_time_stamp"};
     * 
     * public String getTagName(int tag) {
	 *     if (tag < tagNames.length) {
	 *           return tagNames[tag];
	 *     }
	 *     return "unknown_" + String.valueOf(tag);
     * }
     * </pre>
     * @param val
     * @return
     */
    public abstract String getTagName(int val);
    /**
     * Return the decoded value, String, Number, Object.
     * Example:
     * <pre>
     * //For tag id 2
     * private Object unixTimestamp(byte[] data) {
     *     long val = 0;
     *     for (byte b : data) {
     *         val = (val << 8) | (b & 0xFFL);
     *     }
     *     return Long.valueOf(val);
     * }
     * </pre>
     * @param val Tag id
     * @param data binary data
     * @return decoded binary data.
     */
    public abstract Object decodeTag(int val, byte[] data);
}
