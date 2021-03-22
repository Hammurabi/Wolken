package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpServer;
import org.wolkenproject.core.Context;
import org.wolkenproject.utils.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpApp {
    private final HttpServer server;

    protected HttpApp(int port) throws IOException {
        Logger.alert("=============================================");
        Logger.alert("Starting HTTP server");
        Logger.alert("=============================================");

        server = HttpServer.create(new InetSocketAddress(port), 12);
        server.createContext("/", RpcServer::listen);
        server.setExecutor(null);

        paths = new UrlPath[] {
                new UrlPath("content", messenger -> messenger.sendFile("text/html", Context.getInstance().getResourceManager().get("/index.html")), new UrlPath[] {
                }),
        };
        server.start();
    }

    public void stop(int i) {
    }
}
