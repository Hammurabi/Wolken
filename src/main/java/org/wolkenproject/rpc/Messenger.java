package org.wolkenproject.rpc;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
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

    public void sendFile(String file) {
        sendFile("text/html", inputStream);
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
}
