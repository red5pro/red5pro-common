package com.red5pro.mixer.nodes;

import java.util.List;

/**
 * Contains other nodes (children)
 *
 * @author Nate Roe
 */
public interface IContainerNode {
    public List<AbstractNode> getNodes();
}
