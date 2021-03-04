package org.wolkenproject.network;

import org.wolkenproject.core.Context;
import org.wolkenproject.network.messages.VersionMessage;
import org.wolkenproject.utils.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server implements Runnable {
    private ServerSocketChannel socket;
    private Set<Node>           connectedNodes;
    private NetAddress          netAddress;

    public Server() throws IOException {
        socket = ServerSocketChannel.open();
        socket.bind(new InetSocketAddress(Context.getInstance().getNetworkParameters().getPort()));

        socket.configureBlocking(false);
        Context.getInstance().getThreadPool().execute(this::listenForIncomingConnections);

        netAddress = Context.getInstance().getIpAddressList().getAddress(InetAddress.getLocalHost());
        if (netAddress == null)
        {
            netAddress = new NetAddress(InetAddress.getLocalHost(), Context.getInstance().getNetworkParameters().getPort(), Context.getInstance().getNetworkParameters().getServices());
            Context.getInstance().getIpAddressList().addAddress(netAddress);
        }

        Logger.alert("opened port '" + Context.getInstance().getNetworkParameters().getPort() + "' on " + netAddress.getAddress().toString());

        connectedNodes = Collections.synchronizedSet(new LinkedHashSet<>());
        connectToNodes(Context.getInstance().getIpAddressList().getAddresses());
    }

    public boolean connectToNodes(Queue<NetAddress> addresses)
    {
        Logger.alert("establishing outbound connections.");
        int connections = 0;

        for (NetAddress address : addresses)
        {
            try {
                SocketChannel socket = SocketChannel.open();
                socket.bind(new InetSocketAddress(address.getAddress(), address.getPort()));

                Node node = new Node(socket);
                connectedNodes.add(node);

                node.sendMessage(new VersionMessage(Context.getInstance().getNetworkParameters().getVersion(), new VersionInformation(Context.getInstance().getNetworkParameters().getVersion(), VersionInformation.Flags.AllServices, System.currentTimeMillis(), getNetAddress(), address, 0)));

                if (++ connections == Context.getInstance().getNetworkParameters().getMaxAllowedOutboundConnections())
                {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void listenForIncomingConnections()
    {
        Logger.alert("listening for inbound connections.");
        SocketChannel incoming = null;

        while (Context.getInstance().isRunning())
        {
            try {
                incoming = socket.accept();

                if (incoming != null) {
                    if (connectedNodes.size() < (Context.getInstance().getNetworkParameters().getMaxAllowedInboundConnections() + Context.getInstance().getNetworkParameters().getMaxAllowedOutboundConnections()))
                    {
                        connectedNodes.add(new Node(incoming));
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
        while (Context.getInstance().isRunning())
        {
            long currentTime = System.currentTimeMillis();

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
        Iterator<Node> nodeIterator = connectedNodes.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            boolean shouldDisconnect = false;
            boolean isSpammy = false;

            if (node.timeSinceConnected() >= Context.getInstance().getNetworkParameters().getHandshakeTimeout() && !node.hasPerformedHandshake())
            {
                shouldDisconnect = true;
            }

            if (node.getTotalErrorCount() > Context.getInstance().getNetworkParameters().getMaxNetworkErrors()) {
                shouldDisconnect = true;
            }

            if (node.getSpamAverage() > Context.getInstance().getNetworkParameters().getMessageSpamThreshold()) {
                shouldDisconnect = true;
                isSpammy = true;
            }

            if (isSpammy) {
                NetAddress address = Context.getInstance().getIpAddressList().getAddress(node.getInetAddress());
                if (address != null)
                {
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

            if (!shouldDisconnect && !isSpammy)
            {
                if (node.getMessageCache().inboundCacheSize() > Context.getInstance().getNetworkParameters().getMaxCacheSize())
                {
                    NetAddress address = Context.getInstance().getIpAddressList().getAddress(node.getInetAddress());
                    if (address != null)
                    {
                        address.setSpamAverage(node.getSpamAverage());
                    }
                    node.getMessageCache().clearInboundCache();
                }

                node.getMessageCache().clearOutboundCache();
            }
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
        return broadcastRequest(request, Context.getInstance().getNetworkParameters().getMessageTimeout());
    }

    public Message broadcastRequest(Message request, long timeOut) {
        return null;
    }

    public Set<Node> getConnectedNodes() {
        return new LinkedHashSet<>(connectedNodes);
    }
}
