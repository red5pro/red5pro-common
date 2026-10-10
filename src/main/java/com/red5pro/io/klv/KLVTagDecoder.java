package com.red5pro.io.klv;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers KLV decoders by universal label. Call {@link #addKLVDecoder(String, Class)} during startup.
 *
 * @author Andy
 */
public abstract class KLVTagDecoder {

    private static Map<String, Class<? extends KLVTagDecoder>> libraries = new ConcurrentHashMap<>();

    private static Map<String, KLVTagDecoder> cache = new HashMap<>();

    protected final static Logger logger = LoggerFactory.getLogger(KLVTagDecoder.class);

    /**
     * Registers a decoder for a KLV universal label.
     *
     * @param universalLabel decoder label
     * @param decoder decoder implementation
     */
    public static void addKLVDecoder(String universalLabel, Class<? extends KLVTagDecoder> decoder) {
        logger.info("Adding KLV decoder: {} handler: {} ", universalLabel, decoder.getSimpleName());
        libraries.put(universalLabel, decoder);
    }

    /** Returns a cached decoder for the label, creating one when registered. */
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

    /** Returns this decoder's universal label. */
    public abstract String getUniversalLabel();

    /**
     * Returns the name associated with a tag ID.
     *
     * @param val tag ID
     * @return tag name
     */
    public abstract String getTagName(int val);

    /**
     * Decodes a tag value.
     *
     * @param val tag ID
     * @param data encoded value
     * @return decoded value
     */
    public abstract Object decodeTag(int val, byte[] data);
}
