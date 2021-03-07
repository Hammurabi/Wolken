package org.wolkenproject.core.script;

import java.util.Map;

public class MetadataProvider {
    Map<Character, Metadata> metadataMap;

    public Metadata getMetadata(char metadata) {
        return metadataMap.get(metadata);
    }
}
