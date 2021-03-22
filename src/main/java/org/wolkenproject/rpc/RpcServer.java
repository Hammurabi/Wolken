package org.wolkenproject.rpc;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.utils.Logger;
import org.wolkenproject.utils.VoidCallable;
import org.wolkenproject.utils.VoidCallableThrowsT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class RpcServer {
    private HttpServer          server;
    private Context             context;
    private UrlPath[]           paths;
    private Set<Request>        handlers;

    public RpcServer(Context context, int port) throws IOException {
        Logger.alert("=============================================");
        Logger.alert("Starting HTTP server");
        Logger.alert("=============================================");

        server = HttpServer.create(new InetSocketAddress(port), 12);
        handlers = new LinkedHashSet<>();

        onGet("/", response -> response.sendFile("/rpc/index.html"));
        onGet("/content/:filename", response -> response.sendFile("/rpc/${filename}"));

        server.createContext("/", exchange -> {
            String url = exchange.getRequestURI().toString();
            Set<Request> handlers =Context.getInstance().getRPCServer().getHandlers();

            for (Request request : handlers) {
                if (request.submit(exchange, url)) {
                    break;
                }
            }

        });

        server.setExecutor(null);
        server.start();
    }

    public Set<Request> getHandlers() {
        return handlers;
    }

    protected void onGet(String url, VoidCallableThrowsT<Messenger, IOException> function) {
        String requestURL = Messenger.requestURL(url);
        boolean mustMatch = requestURL.equals(url);

        handlers.add(new Request(requestURL, mustMatch, function));
    }

    public void stop() {
        server.stop(0);
    }

    private static void traversePath(String url) {
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
