package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;

import java.io.IOException;
import java.io.InputStream;
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

    public void stop() {
        server.stop(0);
    }

    public static void onBlockMsg(HttpExchange exchange) {
        JSONObject message  = readJson(exchange.getRequestBody());
        String blockId      = message.getString("hash");
        boolean txList      = message.getBoolean("transactions");
        boolean evList      = message.getBoolean("events");

    }

    public static void onTransactionMsg(HttpExchange exchange) {
    }

    public static void onSubmitMsg(HttpExchange exchange) {
    }

    public static final JSONObject readJson(InputStream inputStream) {
        return new JSONObject(readUTF(inputStream));
    }

    private static final String readUTF(InputStream inputStream) {

    }
}
