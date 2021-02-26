package org.wokenproject.network;

import org.wokenproject.network.messages.VerackMessage;
import org.wokenproject.network.messages.VersionMessage;

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

    public boolean isHandshake() {
        return message instanceof VersionMessage || message instanceof VerackMessage;
    }
}
