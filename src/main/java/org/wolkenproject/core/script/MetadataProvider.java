package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;

import java.util.Map;

public class MetadataProvider {
    Map<Character, Metadata> metadataMap;

    public Metadata getMetadata(char metadata) throws UndefClassException {
        return metadataMap.get(metadata);
    }
}
