package com.red5pro.mixer.nodes.video;

import java.util.ArrayList;
import java.util.List;

import com.red5pro.mixer.nodes.AbstractNode;

/**
 * The Compositor renders its child nodes sequentially, overlaying.
 *
 * @author Nate Roe
 */
public class CompositorNode extends AbstractNode {
    private List<AbstractNode> nodes;

    public CompositorNode() {
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
