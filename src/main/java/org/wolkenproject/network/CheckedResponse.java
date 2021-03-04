package org.wolkenproject.network;

public class CheckedResponse {
    private Message message;
    private int     flags;

    public CheckedResponse(Message response, int flags) {
        this.message    = response;
        this.flags      = flags;
    }

    public Message getMessage() {
        return message;
    }

    public int getFlags() {
        return flags;
    }

    public boolean noErrors() {
        return flags == 0;
    }

    public boolean containsFullResponse() {
        return flags == 0 || (flags & ResponseMetadata.ValidationBits.EntireResponse) == ResponseMetadata.ValidationBits.EntireResponse;
    }
}
