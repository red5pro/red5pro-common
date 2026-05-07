package com.red5pro.interstitial.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.red5.server.messaging.IConsumer;
import org.red5.server.messaging.IMessage;
import org.red5.server.messaging.IMessageInput;
import org.red5.server.messaging.OOBControlMessage;

/**
 * Test double for IMessageInput. Replays a fixed list of messages via
 * pullMessage(), rewinds on "seek" OOB control messages, and treats
 * subscribe/unsubscribe as no-ops.
 */
class FakeMessageInput implements IMessageInput {

    private final List<IMessage> source;

    private int pos = 0;

    FakeMessageInput(List<IMessage> messages) {
        this.source = new ArrayList<>(messages);
    }

    @Override
    public IMessage pullMessage() throws IOException {
        if (pos >= source.size()) {
            return null;
        }
        return source.get(pos++);
    }

    @Override
    public IMessage pullMessage(long wait) {
        if (pos >= source.size()) {
            return null;
        }
        return source.get(pos++);
    }

    @Override
    public boolean subscribe(IConsumer consumer, Map<String, Object> paramMap) {
        return true;
    }

    @Override
    public boolean unsubscribe(IConsumer consumer) {
        return true;
    }

    @Override
    public List<IConsumer> getConsumers() {
        return Collections.emptyList();
    }

    @Override
    public void sendOOBControlMessage(IConsumer consumer, OOBControlMessage oobCtrlMsg) {
        if ("seek".equals(oobCtrlMsg.getServiceName())) {
            pos = 0;
            oobCtrlMsg.setResult(Integer.valueOf(0));
        }
    }
}
