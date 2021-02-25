package org.wokenproject.serialization;

import org.wokenproject.serialization.SerializableI;

import java.util.HashMap;
import java.util.Map;

public class SerializationFactory {
    private Map<Integer, SerializableI> magicReferences;

    public SerializationFactory()
    {
        magicReferences = new HashMap<>();
    }
}
