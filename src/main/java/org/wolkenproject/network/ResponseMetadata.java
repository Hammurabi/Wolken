package org.wolkenproject.network;

import org.wolkenproject.network.messages.ResponseMessage;

public interface ResponseMetadata {
    boolean isResponseValid(ResponseMessage responseMessage);
}
