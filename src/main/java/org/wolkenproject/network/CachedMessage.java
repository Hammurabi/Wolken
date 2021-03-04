package org.wolkenproject.network;

import org.wolkenproject.network.messages.VerackMessage;
import org.wolkenproject.network.messages.VersionMessage;

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
