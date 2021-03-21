package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RpcServer {
    private HttpServer server;
    public RpcServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/block", RpcServer::onBlockMsg);
        server.setExecutor(null);
        server.start();
    }

    public static void onBlockMsg(HttpExchange exchange) {
    }
}
