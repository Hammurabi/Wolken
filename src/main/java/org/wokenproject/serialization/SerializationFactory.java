package org.wokenproject.serialization;

import org.wokenproject.serialization.SerializableI;

import java.io.BufferedInputStream;
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

    public <Type extends SerializableI> Type fromStream(BufferedInputStream stream)
    {
        return null;
    }
}
