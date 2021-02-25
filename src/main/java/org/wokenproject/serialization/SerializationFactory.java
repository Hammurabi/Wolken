package org.wokenproject.serialization;

import org.wokenproject.exceptions.InvalidSerialNumberException;
import org.wokenproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SerializationFactory {
    private Map<Class, Integer>         classMagicReferences;
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

    public <Type extends SerializableI> Type fromStream(BufferedInputStream stream) throws IOException {
        byte magicBytes[] = new byte[4];
        stream.read(magicBytes);

        SerializableI serializable  = magicReferences.get(Utils.makeInt(magicBytes));
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
}
