package org.wokenproject.network.messages;

import org.wokenproject.network.Message;

public class NotificationMessage extends Message {
    public NotificationMessage(int version, int type, int count, byte[] content) {
        super(version, Message.Flags.NOTIFY, type, count, content);
    }
}
