package org.wokenproject.network;

import java.util.HashMap;
import java.util.Map;

public class MessageCache {
    private Map<byte[], Integer> messageMap;

    public MessageCache()
    {
        messageMap = new HashMap<>();
    }

    public void setReceivedMessage(Message message)
    {
        byte messageId[] = message.contentHash();

        if (messageMap.containsKey(messageId))
        {
            messageMap.put(messageId, messageMap.get(messageId) + 1);
        }
        else
        {
            messageMap.put(messageId, 1);
        }
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
