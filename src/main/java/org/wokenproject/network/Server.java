package org.wokenproject.network;

import org.wokenproject.core.Context;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class Server {
    private ServerSocket    socket;
    private Set<Node>       connectedNodes;

    public Server()
    {
        Context.getInstance().getThreadPool().execute(this::listenForIncomingConnections);
    }

    public void connectToNodes()
    {
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
