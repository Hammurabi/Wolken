package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenTimeoutException;
import org.wolkenproject.network.messages.Inv;
import org.wolkenproject.network.messages.VersionMessage;
import org.wolkenproject.utils.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements Runnable {
    private ServerSocket        socket;
    private Set<Node>           connectedNodes;
    private NetAddress          netAddress;
    private ReentrantLock       mutex;
    private byte                nonce[];
    private long                upSince;

    public Server(Set<NetAddress> forceConnections) throws IOException {
        socket  = new ServerSocket();
        socket.bind(new InetSocketAddress(Context.getInstance().getNetworkParameters().getPort()));
        upSince = System.currentTimeMillis();
        mutex   = new ReentrantLock();
        nonce   = new byte[20];

        // generate a nonce to know when we self connect
        new SecureRandom().nextBytes(nonce);

        // listen for incoming connections indefinitely
        Context.getInstance().getThreadPool().execute(this::listenForIncomingConnections);

        netAddress = Context.getInstance().getIpAddressList().getAddress(InetAddress.getLocalHost());
        if (netAddress == null)
        {
            netAddress = new NetAddress(InetAddress.getLocalHost(), Context.getInstance().getNetworkParameters().getPort(), Context.getInstance().getNetworkParameters().getServices());
            Context.getInstance().getIpAddressList().addAddress(netAddress);
        }

        Logger.alert("opened port '" + Context.getInstance().getNetworkParameters().getPort() + "' on " + netAddress.getAddress().toString());

        connectedNodes = Collections.synchronizedSet(new LinkedHashSet<>());
        connectToNodes(forceConnections, Context.getInstance().getIpAddressList().getAddresses());
    }

    public boolean connectToNodes(Set<NetAddress> forceConnections, Queue<NetAddress> addresses)
    {
        Logger.alert("establishing outbound connections.");
        int connections = 0;

        for (NetAddress address : forceConnections) {
            int i = connectedNodes.size();
            Logger.alert("attempting to connect to ${a}", address);

            forceConnect(address);
            if (connectedNodes.size() == i) {
                Logger.alert("failed to connect to ${a}", address);
            }
        }

        for (NetAddress address : addresses)
        {
            forceConnect(address);

            if (++ connections == Context.getInstance().getNetworkParameters().getMaxAllowedOutboundConnections())
            {
                return true;
            }
        }

        return false;
    }

    private void forceConnect(InetAddress address, int port) {
        forceConnect(new NetAddress(address, port, 0));
    }

    private void forceConnect(NetAddress address) {
        try {
            Socket socket = new Socket();
            socket.bind(new InetSocketAddress(address.getAddress(), address.getPort()));

            Node node = new Node(socket);
            mutex.lock();
            try {
                connectedNodes.add(node);
                Logger.alert("connected to ${s}", address);
            } finally {
                mutex.unlock();
            }

            node.sendMessage(new VersionMessage(
                    Context.getInstance().getNetworkParameters().getVersion(),
                    new VersionInformation(
                            Context.getInstance().getNetworkParameters().getVersion(),
                            VersionInformation.Flags.AllServices,
                            System.currentTimeMillis(),
                            getNetAddress(),
                            address,
                            Context.getInstance().getBlockChain().getHeight(),
                            nonce)));
        } catch (IOException e) {
        }
    }

    private void listenForIncomingConnections()
    {
        Logger.alert("listening for inbound connections.");
        Socket incoming = null;

        while (Context.getInstance().isRunning())
        {
            try {
                incoming = socket.accept();

                if (incoming != null) {
                    if (connectedNodes.size() < (Context.getInstance().getNetworkParameters().getMaxAllowedInboundConnections() + Context.getInstance().getNetworkParameters().getMaxAllowedOutboundConnections()))
                    {
                        mutex.lock();
                        try {
                            connectedNodes.add(new Node(incoming));
                        } finally {
                            mutex.unlock();
                        }
                    }
                    else
                    {
                        incoming.close();
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void run() {
        // we don't need to start checks right away
        long lastCheck = System.currentTimeMillis();
        long lastNotif = 0;

        while (Context.getInstance().isRunning())
        {
            long currentTime = System.currentTimeMillis();
            Set<Node> connectedNodes = getConnectedNodes();

            if (currentTime - lastNotif >= 10_000) {
                Logger.alert("server uptime: ${m}ms", System.currentTimeMillis() - upSince);
                Logger.alert("connected: ${d}", connectedNodes.size());
                Logger.alert("list: ${s}", connectedNodes);

                lastNotif = System.currentTimeMillis();
            }

            if (currentTime - lastCheck >= 30_000)
            {
                runMaintenanceChecks();
                lastCheck = currentTime;
            }

            for (Node node : connectedNodes) {
                node.read();
            }

            for (Node node : connectedNodes)
            {
                CachedMessage message = node.listenForMessage();

                if (message != null) {
                    if (!message.isSpam())
                    {
                        if (!node.hasPerformedHandshake() && !message.isHandshake())
                        {
                            // ignore message
                            continue;
                        }

                        message.getMessage().executePayload(this, node);
                    }
                }
            }

            for (Node node : connectedNodes)
            {
                node.flush();
            }
        }
    }

    private void runMaintenanceChecks() {
        mutex.lock();
        try {
            Iterator<Node> nodeIterator = connectedNodes.iterator();
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.next();
                boolean shouldDisconnect = false;
                boolean isSpammy = false;

                if (!node.hasPerformedHandshake() && node.timeSinceConnected() >= Context.getInstance().getNetworkParameters().getHandshakeTimeout()) {
                    shouldDisconnect = true;
                }

                if (node.getTotalErrorCount() > Context.getInstance().getNetworkParameters().getMaxNetworkErrors()) {
                    shouldDisconnect = true;
                }

                if (node.getSpamAverage() >= Context.getInstance().getNetworkParameters().getMessageSpamThreshold()) {
                    shouldDisconnect = true;
                    isSpammy = true;
                }

                if (isSpammy) {
                    NetAddress address = Context.getInstance().getIpAddressList().getAddress(node.getInetAddress());
                    if (address != null) {
                        address.setSpamAverage(node.getSpamAverage());
                    }
                }

                if (shouldDisconnect) {
                    nodeIterator.remove();
                    try {
                        node.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (!shouldDisconnect && !isSpammy) {
                    if (node.getMessageCache().inboundCacheSize() > Context.getInstance().getNetworkParameters().getMaxCacheSize()) {
                        NetAddress address = Context.getInstance().getIpAddressList().getAddress(node.getInetAddress());
                        if (address != null) {
                            address.setSpamAverage(node.getSpamAverage());
                        }
                        node.getMessageCache().clearInboundCache();
                    }

                    node.getMessageCache().clearOutboundCache();
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    public void shutdown() {
        Logger.alert("closing connections.");

        Iterator<Node> nodeIterator = connectedNodes.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            try {
                node.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Logger.alert("closed connections.");
    }

    public NetAddress getNetAddress() {
        return netAddress;
    }

    public Message broadcastRequest(Message request) {
        return broadcastRequest(request, true);
    }

    public Message broadcastRequest(Message request, boolean fullResponse) {
        return broadcastRequest(request, fullResponse, Context.getInstance().getNetworkParameters().getMessageTimeout());
    }

    public Message broadcastRequest(Message request, boolean fullResponse, long timeOut) {
        Set<Node> connectedNodes = getConnectedNodes();

        for (Node node : connectedNodes) {
            try {
                CheckedResponse response = node.getResponse(request, timeOut);
                if (response != null) {
                    if (response.noErrors()) {
                        return response.getMessage();
                    } else if (response.containsPartialResponse()) {
                        if (fullResponse) {
                            continue;
                        }

                        return response.getMessage();
                    }
                }
            } catch (WolkenTimeoutException e) {
            }
        }

        return null;
    }

    public Set<Node> getConnectedNodes() {
        return new LinkedHashSet<>(connectedNodes);
    }

    public void broadcast(Message message) {
        Set<Node> connectedNodes = getConnectedNodes();

        for (Node node : connectedNodes) {
            node.sendMessage(message);
        }
    }

    public byte[] getNonce() {
        return nonce;
    }
}
