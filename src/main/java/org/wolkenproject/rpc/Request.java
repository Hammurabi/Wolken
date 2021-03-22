package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpExchange;
import org.wolkenproject.utils.VoidCallableThrowsT;

import java.io.IOException;

public class Request {
    private String  pattern;
    private boolean mustMatch;
    private VoidCallableThrowsT<Messenger, IOException> function;

    public Request(String pattern, boolean mustMatch, VoidCallableThrowsT<Messenger, IOException> function) {
        this.pattern = pattern;
        this.mustMatch = mustMatch;
        this.function = function;
    }

    public boolean submit(HttpExchange exchange, String url) throws IOException {
        boolean isMatch = false;

        if (mustMatch) {
            isMatch = pattern.equals(url);
        } else {
            isMatch = url.startsWith(pattern);
        }

        if (isMatch) {
            function.call(new Messenger(exchange, url));
        }

        return isMatch;
    }
}
