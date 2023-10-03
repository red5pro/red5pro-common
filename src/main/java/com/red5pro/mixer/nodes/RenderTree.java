package com.red5pro.mixer.nodes;

public class RenderTree {
    private AbstractNode rootVideoNode;

    private AbstractNode rootAudioNode;

    public RenderTree() {
    }

    public RenderTree(AbstractNode rootVideoNode, AbstractNode rootAudioNode) {
        this.rootVideoNode = rootVideoNode;
        this.rootAudioNode = rootAudioNode;
    }

    public AbstractNode getRootVideoNode() {
        return rootVideoNode;
    }

    public void setRootVideoNode(AbstractNode rootVideoNode) {
        this.rootVideoNode = rootVideoNode;
    }

    public AbstractNode getRootAudioNode() {
        return rootAudioNode;
    }

    public void setRootAudioNode(AbstractNode rootAudioNode) {
        this.rootAudioNode = rootAudioNode;
    }
}
