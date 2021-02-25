package org.wokenproject.network;

public class CachedMessage {
    private Message message;
    private boolean isSpam;

    public CachedMessage(Message message, boolean isSpam)
    {
        this.message    = message;
        this.isSpam     = isSpam;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isSpam()
    {
        return isSpam;
    }
}
