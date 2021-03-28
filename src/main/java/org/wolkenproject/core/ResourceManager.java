package org.wolkenproject.core;

import java.io.IOException;
import java.io.InputStream;

public class ResourceManager {
    public InputStream get(String path) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("'" + path + "' could no be reached.");
        }

        return inputStream;
    }
}
