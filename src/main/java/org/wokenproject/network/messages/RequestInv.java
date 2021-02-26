package org.wokenproject.network.messages;

import org.wokenproject.core.Context;
import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.network.Message;
import org.wokenproject.network.Node;
import org.wokenproject.network.Server;
import org.wokenproject.serialization.SerializableI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestInv extends Message {
    public RequestInv(int version) {
        super(version, Flags.Request);
    }

    @Override
    public void executePayload(Server server, Node node) {
        try {
            node.sendMessage(new Inv(Context.getInstance().getNetworkParameters().getVersion(), Inv.Type.Block, null));
        } catch (WolkenException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeContents(OutputStream stream) throws IOException, WolkenException {
    }

    @Override
    public void readContents(InputStream stream) throws IOException, WolkenException {
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new RequestInv(0);
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(RequestInv.class);
    }
}
