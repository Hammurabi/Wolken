package org.wolkenproject.core.script;

import org.wolkenproject.encoders.Base16;
import org.wolkenproject.exceptions.UndefClassException;

import java.util.Map;

public class ClassProvider {
    Map<Character, Metadata> metadataMap;

    public Metadata getMetadata(char metadata) throws UndefClassException {
        if (!metadataMap.containsKey(metadata)) {
            throw new UndefClassException("no metadata found for '" + metadata + "'.");
        }

        return metadataMap.get(metadata);
    }

    public MochaClass getDefaultMochaClass() {
        MochaClass mochaClass = new MochaClass();
        mochaClass.setName("MochaObject");
        mochaClass.addFunction("hashCode", (mem)->{ return new MochaByteArray(mem.popStack().checksum()); });
        mochaClass.addFunction("toString", (mem)->{ return new MochaString(Base16.encode(mem.popStack().checksum())); });

        return mochaClass;
    }
}
