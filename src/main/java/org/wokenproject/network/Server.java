package org.wokenproject.network;

import org.wokenproject.core.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class Server {
    private ServerSocket    socket;
    private Set<Node>       connectedNodes;

    public Server()
    {
        Context.getInstance().getThreadPool().execute(this::listenForIncomingConnections);
        connectToNodes(Context.getInstance().getIpAddressList());
    }

    public boolean connectToNodes(Set<NetAddress> addresses)
    {
        int connections = 0;

        for (NetAddress address : addresses)
        {
            try {
                Socket socket = new Socket(address.getAddress(), address.getPort());
                connectedNodes.add(new Node(socket));

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
        Socket incoming = null;
        while (Context.getInstance().isRunning())
        {
            try {
                incoming = socket.accept();
                if (connectedNodes.size() < (Context.getInstance().getNetworkParameters().getMaxAllowedInboundConnections() + Context.getInstance().getNetworkParameters().getMaxAllowedOutboundConnections()))
                {
                    connectedNodes.add(new Node(incoming));
                }
                else
                {
                    incoming.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
