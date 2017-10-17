/*
 * https://account.red5pro.com/assets/LICENSE.txt
 */
package com.red5pro.override.cauldron.brews;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class signals which native module is loaded and provides a concurrent queue to pass key/value pairs during processing.
 * 
 * @author Andy Shaules
 */
public class Potion extends LinkedBlockingQueue<Ingredient> {

    private static final long serialVersionUID = -4967121322278479390L;

    private String guid;

    /**
     * <pre>
     * streamProcessorStart(IProStream stream){
     *     Potion p = new Potion("face");
     *     p.add(new Ingredient("background",0xFFFFFFFF));
     *     p.add(new Ingredient("maskShape","rect"));
     *     stream.setPotion(p);
     * }
     * </pre>
     * @param guid globally unique id
     */
    public Potion(String guid) {
        this.guid = guid;
    }

    /**
     * Returns the guid corresponding to the native module that should be loaded.
     * 
     * @return guid
     */
    public String getGuid() {
        return guid;
    }
}
