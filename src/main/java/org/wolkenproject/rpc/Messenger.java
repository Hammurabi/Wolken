package org.wolkenproject.rpc;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.wolkenproject.core.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Messenger {
    private HttpExchange        exchange;
    private String              url;
    private String              query;
    private Map<String, String> regexMatches;

    public Messenger(HttpExchange exchange, String urlMatcher) {
        this.exchange   = exchange;

        query           = exchange.getRequestURI().getQuery();

        if (query == null) {
            query = "";
        }

        url             = exchange.getRequestURI().toString().replace(query, "");

        if (urlMatcher.contains(":")) {
            String surl[] = url.split("/");
            String regex[]= urlMatcher.split("/");

            for (int i = 0; i < regex.length; i ++) {
                if (regex[i].startsWith(":")) {
                    regexMatches.put(regex[i].substring(1), surl[i]);
                }
            }
        }

        regexMatches    = new HashMap<>();
    }

    public void sendFile(String file) throws IOException {
        if (file.endsWith("json")) {
            sendFile("application/json", Context.getInstance().getResourceManager().get(file));
        } else if (file.endsWith("html")) {
            sendFile("text/html", Context.getInstance().getResourceManager().get(file));
        } else if (file.endsWith("css")) {
            sendFile("text/css", Context.getInstance().getResourceManager().get(file));
        } else if (file.endsWith("png")) {
            sendFile("image/png", Context.getInstance().getResourceManager().get(file));
        } else if (file.endsWith("jpg") || file.endsWith("jpeg")) {
            sendFile("image/jpeg", Context.getInstance().getResourceManager().get(file));
        } else if (file.endsWith("gif")) {
            sendFile("image/gif", Context.getInstance().getResourceManager().get(file));
        } else if (file.endsWith("js")) {
            sendFile("text/javascript", Context.getInstance().getResourceManager().get(file));
        }
    }

    public void sendFile(String contentType, InputStream inputStream) {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", contentType);
    }

    public String getQuery() {
        return query;
    }

    public String getUrl() {
        return url;
    }

    public String get(String query) {
        if (regexMatches.containsKey(query)) {
            return regexMatches.get(query);
        }

        return "";
    }

    private static final String readUTF(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        StringBuilder builder = new StringBuilder();
        while (( line = reader.readLine() ) != null ) {
            builder.append(line).append("\n");
        }

        reader.close();
        return builder.toString();
    }
}
