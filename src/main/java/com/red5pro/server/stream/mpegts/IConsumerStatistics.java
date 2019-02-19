package com.red5pro.server.stream.mpegts;

import java.util.concurrent.CopyOnWriteArraySet;

public interface IConsumerStatistics {
    static final CopyOnWriteArraySet<IConsumerStatistics> listeners = new CopyOnWriteArraySet<>();
    
    public void receiveStats(String remoteAddress,String path, String name, long bytes );
    
}
