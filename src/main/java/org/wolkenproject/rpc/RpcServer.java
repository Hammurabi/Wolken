package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.wolkenproject.core.Context;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RpcServer {
    private HttpServer  server;
    private Context     context;

    public RpcServer(Context context, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 12);
        server.createContext("/submit", RpcServer::onSubmitMsg);
        server.createContext("/tx", RpcServer::onTransactionMsg);
        server.createContext("/block", RpcServer::onBlockMsg);
        server.setExecutor(null);
        server.start();
    }

    public static void onBlockMsg(HttpExchange exchange) {
    }

    public static void onTransactionMsg(HttpExchange exchange) {
    }

    public static void onSubmitMsg(HttpExchange exchange) {
    }
}
