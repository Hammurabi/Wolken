package org.wokenproject.network;

import java.util.HashMap;
import java.util.Map;

public class MessageCache {
    private Map<byte[], Integer> receivedMessages;

    public MessageCache() {
        receivedMessages = new HashMap<>();
    }

    public void setReceivedMessage(Message message) {
        byte messageId[] = message.contentHash();

        if (receivedMessages.containsKey(messageId)) {
            receivedMessages.put(messageId, receivedMessages.get(messageId) + 1);
        } else {
            receivedMessages.put(messageId, 1);
        }
    }

    private int numTimesReceived(Message message) {
        return 0;
    }

    public boolean shouldSend(Message message) {
        return false;
    }

    public double getAverageSpam() {
        return 0;
    }
}