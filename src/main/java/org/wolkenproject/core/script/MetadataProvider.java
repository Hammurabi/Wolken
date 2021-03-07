package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;

import java.util.Map;

public class MetadataProvider {
    Map<Character, Metadata> metadataMap;

    public Metadata getMetadata(char metadata) throws UndefClassException {
        if (!metadataMap.containsKey(metadata)) {
            throw new UndefClassException("no metadata found for '" + metadata + "'.");
        }
        
        return metadataMap.get(metadata);
    }
}
