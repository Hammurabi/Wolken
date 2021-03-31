package org.wolkenproject.papaya.compiler;

import java.util.*;

public class Grammar {
    private final Map<String, List<String>> ruleMap;

    public Grammar() {
        this.ruleMap = new LinkedHashMap<>();
    }

    public void addRule(String name, String ...rule) {
        ruleMap.put(name, Arrays.asList(rule));
    }
}
