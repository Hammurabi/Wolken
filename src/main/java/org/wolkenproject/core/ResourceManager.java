package org.wolkenproject.core;

import org.wolkenproject.utils.FileService;

import java.io.IOException;
import java.io.InputStream;

public class ResourceManager {
    public InputStream get(String path) throws IOException {
        return getClass().getResourceAsStream(path);
    }
}
