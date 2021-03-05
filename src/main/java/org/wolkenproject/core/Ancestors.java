package org.wolkenproject.core;

import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

public class Ancestors extends SerializableI {
    private Set<byte[]> hashes;

    public Ancestors() {
        hashes = new LinkedHashSet<>();
    }

    public void fill(BlockIndex tip) {
        int latest      = tip.getHeight() - 1;
        int earliest    = Math.max(0, latest - 16384);
        int total       = latest - earliest;

        int jump        = Math.max(1, total / 128);

        int current     = latest;
        while (current > earliest) {
            byte hash[] = Context.getInstance().getDatabase().findBlockHash(current);
            if (hash != null) {
                hashes.add(hash);
            }

            current -= jump;
        }
    }

    public byte[] findCommon() {
        for (byte[] hash : hashes) {
            if (Context.getInstance().getDatabase().checkBlockExists(hash)) {
                return hash;
            }
        }

        return null;
    }

    @Override
    public void write(OutputStream stream) throws IOException, WolkenException {
        Utils.writeInt(hashes.size(), stream);
        for (byte hash[] : hashes) {
            stream.write(hash);
        }
    }

    @Override
    public void read(InputStream stream) throws IOException, WolkenException {
        byte buffer[] = new byte[4];
        stream.read(buffer, 0, 4);
        int length = Utils.makeInt(buffer);

        for (int i = 0; i < length; i ++) {
            byte hash[] = new byte[Block.UniqueIdentifierLength];
            stream.read(hash);
            hashes.add(hash);
        }
    }

    @Override
    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
        return (Type) new Ancestors();
    }

    @Override
    public int getSerialNumber() {
        return Context.getInstance().getSerialFactory().getSerialNumber(Ancestors.class);
    }
}
