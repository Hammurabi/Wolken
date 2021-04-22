package org.wolkenproject.core;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceManager {
    public InputStream get(String path) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("'" + path + "' could no be reached.");
        }

        return inputStream;
    }

    public JSONObject getJson(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(get(path)));
        StringBuilder builder = new StringBuilder();
        String line = "";

        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }

        return new JSONObject(builder.toString());
    }
}
