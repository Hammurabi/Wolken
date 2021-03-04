package org.wolkenproject.serialization;

import org.wolkenproject.exceptions.InvalidSerialNumberException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SerializationFactory {
    private Map<Class<?>, Integer>      classMagicReferences;
    private Map<Integer, SerializableI> magicReferences;

    public SerializationFactory()
    {
        classMagicReferences = new HashMap<>();
        magicReferences = new HashMap<>();
    }

    /*
        register an instance of serializable object.
     */
    public void registerClass(Class<?> classType, SerializableI serializableInstance)
    {
        int magic = 1 + classMagicReferences.size();
        classMagicReferences.put(classType, magic);
        magicReferences.put(magic, serializableInstance);
    }

    public <Type extends SerializableI> Type fromStream(Class<?> classType, InputStream stream) throws IOException, WolkenException {
        return fromStream(getSerialNumber(classType), stream);
    }

    public <Type extends SerializableI> Type fromStream(InputStream stream) throws IOException, WolkenException {
        byte magicBytes[] = new byte[4];
        stream.read(magicBytes);

        return fromStream(Utils.makeInt(magicBytes), stream);
    }

    public <Type extends SerializableI> Type fromStream(int magic, InputStream stream) throws IOException, WolkenException {
        SerializableI serializable  = magicReferences.get(validateMagicNumber(magic));

        Type result                 = serializable.newInstance();
        result.read(stream);

        return result;
    }

    private int validateMagicNumber(int magic) throws InvalidSerialNumberException {
        if (magicReferences.containsKey(magic))
        {
            return magic;
        }

        throw new InvalidSerialNumberException("'" + magic + "' is an invalid serial number.");
    }

    public int getSerialNumber(Class<?> classType) {
        return classMagicReferences.get(classType);
    }
}
