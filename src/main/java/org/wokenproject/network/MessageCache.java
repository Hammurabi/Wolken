package org.wokenproject.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageCache {
    private Map<byte[], Integer> receivedMessages;
    private Map<byte[], Integer> sentMessages;

    public MessageCache() {
        receivedMessages = new HashMap<>();
    }

    public void cacheReceivedMessage(Message message) {
        byte messageId[] = message.contentHash();

        if (receivedMessages.containsKey(messageId)) {
            receivedMessages.put(messageId, receivedMessages.get(messageId) + 1);
        } else {
            receivedMessages.put(messageId, 1);
        }
    }

    private int numTimesReceived(Message message) {
        byte messageId[] = message.contentHash();

        if (receivedMessages.containsKey(messageId)) {
            return receivedMessages.get(messageId);
        }

        return 0;
    }

    public boolean shouldSend(Message message) {
        byte messageId[] = message.contentHash();
        int timesSent = 0;

        if (sentMessages.containsKey(messageId)) {
            timesSent = sentMessages.get(messageId);
            sentMessages.put(messageId, timesSent + 1);
        } else {
            sentMessages.put(messageId, 1);
        }

        return timesSent < 4;
    }

    public double getAverageSpam() {
        double numTimes = 0;
        for (Integer integer : receivedMessages.values())
        {
            numTimes += integer.doubleValue();
        }

        return (numTimes / receivedMessages.size()) - 1.0;
    }
}