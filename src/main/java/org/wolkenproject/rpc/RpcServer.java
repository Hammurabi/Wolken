package org.wolkenproject.rpc;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wolkenproject.core.BlockIndex;
import org.wolkenproject.core.Context;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.Node;
import org.wolkenproject.utils.Logger;
import org.wolkenproject.utils.VoidCallableThrowsT;
import org.wolkenproject.wallet.Wallet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {
    private HttpServer          server;
    private Context             context;
    private UrlPath[]           paths;
    private Set<Request>        handlers;
    private ExecutorService     executor;

    public RpcServer(Context context, int port) throws IOException {
        Logger.alert("=============================================");
        Logger.alert("Starting HTTP server");
        Logger.alert("=============================================");

        server = HttpServer.create(new InetSocketAddress(port), 12);
        handlers = new LinkedHashSet<>();

        onGet("/", response -> response.sendFile("/rpc/index.html"));
        onGet("/home", response -> response.sendFile("/rpc/index.html"));
        onGet("/dashboard", response -> response.sendFile("/rpc/dashboard.html"));
        onGet("/portfolio", response -> response.sendFile("/rpc/portfolio.html"));
        onGet("/login", response -> response.sendFile("/rpc/login.html"));
        onGet("/api", RpcServer::apiRequest);
        onGet("/content/:filename", response -> response.sendFile("/rpc/${filename}"));

        server.createContext("/", exchange -> {
            Set<Request> handlers =Context.getInstance().getRPCServer().getHandlers();
            String query           = exchange.getRequestURI().getQuery();

            if (query == null) {
                query = "";
            }

            String url             = exchange.getRequestURI().toString();

            if (!query.isEmpty()) {
                url = url.replace(query, "");
                url = url.substring(0, url.length() - 1);
            }

            for (Request request : handlers) {
                if (request.submit(url)) {
                    request.call(exchange, url, query);
                    break;
                }
            }

        });

        executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        server.start();
    }

    public Set<Request> getHandlers() {
        return handlers;
    }

    public static void apiRequest(Messenger msg) throws IOException {
        JSONObject request = msg.getFormattedQuery();
        JSONObject response= new JSONObject();
        String requestType = request.getString("request");

        if (request.getString("request").equals("close")) {
            String password = request.getString("password");
            response.put("response", "success");
            msg.send("application/json", response.toString().getBytes());
            Context.getInstance().shutDown();
            return;
        } else if (requestType.equals("createwallet")) {
            if (!request.has("name")) {
                response.put("response", "failed");
                response.put("reason", "'createwallet' command requires an argument 'name'.");
            } else {
                String name = request.getString("name");
                if (Context.getInstance().getDatabase().checkWalletExists(name)) {
                    response.put("response", "failed");
                    response.put("reason", "a wallet with the name '" + name + "' already exists.");
                } else {
                    response.put("response", "success");
                    String password = request.getString("password");
                    char pass[]     = password.toCharArray();
                    if (password.isEmpty()) {
                        pass        = null;
                    }

                    try {
                        Wallet wallet = new Wallet(name, pass);
                        Context.getInstance().getDatabase().storeWallet(wallet);
                        response.put("content", wallet.toJson());
                    } catch (WolkenException e) {
                        response.put("response", "failed");
                        response.put("reason", e.getMessage());
                    }
                }
            }
        }
        else if (request.getString("request").equals("createwallet")) {
        } else if (request.getString("request").equals("gettx")) {
        } else if (request.getString("request").equals("server")) {
            response.put("response", "success");
            Set<Node> nodes = Context.getInstance().getServer().getConnectedNodes();

            if (request.has("connected") && request.getBoolean("connected")) {
                response.put("numconnected", nodes.size());
            }

            if (request.has("nodes") && request.getBoolean("nodes")) {
                JSONArray array = new JSONArray();
                int counter = 0;
                for (Node node : nodes) {
                    array.put(counter ++, node.toJson());
                }
                response.put("nodes", array);
            }
        }

        msg.send("application/json", response.toString().getBytes());
    }

    protected void onGet(String requestURL, VoidCallableThrowsT<Messenger, IOException> function) {
        boolean mustMatch = !requestURL.contains(":");

        handlers.add(new Request(requestURL, mustMatch, function));
    }

    public void stop() {
        Logger.alert("stopping rpc server.");
        server.stop(0);
        executor.shutdownNow();
        Logger.alert("rpc server stopped.");
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
