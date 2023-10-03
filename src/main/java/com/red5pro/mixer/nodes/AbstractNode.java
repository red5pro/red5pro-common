package com.red5pro.mixer.nodes;

/**
 * The base class of all Mixer Nodes
 *
 * @author Nate Roe
 */
public class AbstractNode {
    /**
     * Every node has a type to help with serialization
     */
    private final String node;

    @SuppressWarnings("unused") // used by native
    private transient final long nativePeerId = 0;

    protected AbstractNode() {
        node = this.getClass().getSimpleName();
    }

    public String getNode() {
        return node;
    }

}
