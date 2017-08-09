/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.plugin;

import java.util.List;

import org.springframework.core.io.Resource;

/**
 * Routes service requests from external sources through the selected plugin. 
 * 
 * @author Paul Gregoire
 */
public interface IPluginService {

    /**
     * Handle an incoming request with supplied args.
     * 
     * @param args method args
     * @return true if successful and false otherwise
     */
    boolean handleRequest(Object... args);

    
    /**
     * Handle an incoming request with supplied args.
     * 
     * @param args method args
     * @return list of resources or empty list if no matching items found
     */
    List<Resource> handleResourceRequest(Object... args);

}
