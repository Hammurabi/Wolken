package org.wolkenproject.rpc;

import java.io.InputStream;

public class Messenger {
    public void sendFile(InputStream inputStream) {
        sendFile("text/html", inputStream);
    }

    public void sendFile(String contentType, InputStream inputStream) {
    }
}
