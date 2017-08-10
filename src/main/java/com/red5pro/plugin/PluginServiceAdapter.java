/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.plugin;

import java.util.Collections;
import java.util.List;

import org.springframework.core.io.Resource;

/**
 * Plugin service adapter to make implementation easier.
 * 
 * @author Paul Gregoire (paul@infrared5.com)
 */
public class PluginServiceAdapter implements IPluginService {

    /** {@inheritDoc} */
    @Override
    public boolean handleRequest(Object... args) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public List<Resource> handleResourceRequest(Object... args) {
        return Collections.emptyList();
    }

}
