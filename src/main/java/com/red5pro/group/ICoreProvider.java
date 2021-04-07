package com.red5pro.group;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provide logical handlers for Composition objects.
 * 
 * @author Andy
 *
 */
public interface ICoreProvider {
    /**
     * Application developers add handlers here.
     */
    public static List<ICoreProvider> registry = new CopyOnWriteArrayList<>();

    /**
     * Return the IGroupCore impl or null.
     * 
     * @param clazz
     * @return
     */
    IGroupCore resolve(String clazz);

}
