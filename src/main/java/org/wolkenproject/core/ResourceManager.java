package org.wolkenproject.core;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceManager {
    public static InputStream get(String path) throws IOException {
        InputStream inputStream = ResourceManager.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("'" + path + "' could no be reached.");
        }

        return inputStream;
    }

    public static JSONObject getJson(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(get(path)));
        StringBuilder builder = new StringBuilder();
        String line = "";

        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }

        return new JSONObject(builder.toString());
    }

    public static String getString(String path) throws IOException {
        StringBuilder string = new StringBuilder();
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(ResourceManager.class.getResourceAsStream(path)));
        while ((line = reader.readLine()) != null) {
            string.append(line).append("\n");
        }
        reader.close();

        return string.toString();
    }
}
