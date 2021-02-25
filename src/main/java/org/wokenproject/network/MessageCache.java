package org.wokenproject.network;

public class MessageCache {
    public void setReceivedMessage(byte[] hash)
    {
    }

    private int numTimesReceived(byte[] hash)
    {
        return 0;
    }

    public boolean shouldSend(Message message)
    {
        return false;
    }
}
