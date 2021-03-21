package org.wolkenproject.rpc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.encoders.Base16;

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

    public static void onBlockMsg(HttpExchange exchange) throws IOException {
        JSONObject message  = readJson(exchange.getRequestBody());
        String blockId      = message.getString("hash");
        boolean txList      = message.getBoolean("transactions");
        boolean txHash      = message.getBoolean("only-txid");
        boolean evList      = message.getBoolean("events");
        boolean evHash      = message.getBoolean("only-evid");
        boolean txEvt       = message.getBoolean("format");

        byte blockHash[]    = Base16.decode(blockId);

        if (Context.getInstance().getDatabase().checkBlockExists(blockHash)) {
            BlockIndex block= Context.getInstance().getDatabase().findBlock(blockHash);

            JSONObject response = new JSONObject();
            response.put("type", "success");
            response.put("block", block.toJson(txList, txHash, evList, evHash, txEvt));
            sendResponse(200, response, exchange);
        } else {
            JSONObject response = new JSONObject();
            response.put("type", "fail");
            response.put("reason", "could not find requested block");
            sendResponse(200, response, exchange);
        }
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

    private static final void sendResponse(int responseCode, JSONObject response, HttpExchange exchange) throws IOException {
        byte actualResponse[] = response.toString().getBytes();
        exchange.sendResponseHeaders(responseCode, actualResponse.length);
        exchange.getResponseBody().write(actualResponse);
    }
}
