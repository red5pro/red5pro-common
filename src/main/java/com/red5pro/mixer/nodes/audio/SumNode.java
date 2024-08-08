package com.red5pro.mixer.nodes.audio;

import java.util.ArrayList;
import java.util.List;

import com.red5pro.mixer.nodes.AbstractNode;

/**
 * The output is the sum (the stereo mix) of the given nodes.
 *
 * @author Nate Roe
 */
public class SumNode extends AbstractNode {
    private List<AbstractNode> nodes;

    public SumNode() {
        super();
        nodes = new ArrayList<>();
    }

    public List<AbstractNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<AbstractNode> nodes) {
        this.nodes = nodes;
    }
}
