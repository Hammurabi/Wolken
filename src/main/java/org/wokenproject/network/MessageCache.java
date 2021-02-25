package org.wokenproject.network;

import java.util.HashMap;
import java.util.Map;

public class MessageCache {
    private Map<byte[], Message> messageMap;

    public MessageCache()
    {
        messageMap = new HashMap<>();
    }

    public void setReceivedMessage(Message message)
    {
    }

    private int numTimesReceived(Message message)
    {
        return 0;
    }

    public boolean shouldSend(Message message)
    {
        return false;
    }

    public double getAverageSpam() {
        return 0;
    }
}
