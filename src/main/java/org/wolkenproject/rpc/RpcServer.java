package org.wolkenproject.rpc;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.utils.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class RpcServer {
    private HttpServer          server;
    private Context             context;
    private UrlPath[]           paths;

    public RpcServer(Context context, int port) throws IOException {
        Logger.alert("=============================================");
        Logger.alert("Starting HTTP server");
        Logger.alert("=============================================");

        server = HttpServer.create(new InetSocketAddress(port), 12);
        server.createContext("/", RpcServer::listen);
        server.setExecutor(null);

        paths = new UrlPath[] {
                new UrlPath("content", messenger -> messenger.sendFile("text/html", Context.getInstance().getResourceManager().get("/index.html")), new UrlPath[] {
                }),
                new UrlPath("api", new UrlPath[] {}),
        };
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void listen(HttpExchange exchange) throws IOException {
        String query    = exchange.getRequestURI().getQuery();
        String url      = exchange.getRequestURI().toString().replace(query, "");

        Messenger message = new Messenger(exchange, url, query);
        traversePath(url);

        if (url.equals("/")) {
            // return index
            sendResponse(200, readUTF(Context.getInstance().getResourceManager().get("/index.html")), exchange);
        } else {
            String surl[]   = url.split("/");
            if (surl[0].equals("content")) {
            } else if (surl[0].equals("api")) {
            }
        }
    }

    public static void onContentMsg(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "text/html");
    }

    public static void onApiMsg(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json");
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

        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/json");

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

    public static void onTransactionMsg(HttpExchange exchange) throws IOException {
        JSONObject message  = readJson(exchange.getRequestBody());
        String txId         = message.getString("hash");
        boolean evList      = message.getBoolean("events");
        boolean evHash      = message.getBoolean("only-evid");

        byte txHash[]       = Base16.decode(txId);

        if (Context.getInstance().getDatabase().checkTransactionExists(txHash)) {
            Transaction transaction = Context.getInstance().getDatabase().findTransaction(txHash);

            JSONObject response = new JSONObject();
            response.put("type", "success");
            response.put("transaction", transaction.toJson(evList, evHash));
            sendResponse(200, response, exchange);
        } else {
            JSONObject response = new JSONObject();
            response.put("type", "fail");
            response.put("reason", "could not find requested block");
            sendResponse(200, response, exchange);
        }
    }

    public static void onSubmitMsg(HttpExchange exchange) {
    }

    public static void onWalletMsg(HttpExchange exchange) {
    }

    public static final JSONObject readJson(InputStream inputStream) throws IOException {
        return new JSONObject(readUTF(inputStream));
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

    private static final void sendResponse(int responseCode, JSONObject response, HttpExchange exchange) throws IOException {
        sendResponse(responseCode, response.toString(), exchange);
    }

    private static final void sendResponse(int responseCode, String response, HttpExchange exchange) throws IOException {
        byte actualResponse[] = response.getBytes();
        exchange.sendResponseHeaders(responseCode, actualResponse.length);
        exchange.getResponseBody().write(actualResponse);
    }

    public UrlPath[] getPaths() {
        return paths;
    }
}
