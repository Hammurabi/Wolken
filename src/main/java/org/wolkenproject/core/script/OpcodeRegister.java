package org.wolkenproject.core.script;

import org.wolkenproject.core.script.internal.MochaCallable;
import org.wolkenproject.utils.VoidCallable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class OpcodeRegister {
    private Map<String, Opcode>     opcodeNameMap;
    private Map<Integer, Opcode>    opcodeMap;
    private Set<Opcode>             opcodeSet;

    public OpcodeRegister() {
        opcodeNameMap = new HashMap<>();
        opcodeMap = new HashMap<>();
        opcodeSet = new LinkedHashSet<>();
    }

    // register an opcode into the vm
    public OpcodeRegister registerOp(String name, String description, int numArgs, VoidCallable<Scope> callable) {
        Opcode opcode = new Opcode(name, description, "", opcodeSet.size(), numArgs, callable);
        opcodeNameMap.put(name, opcode);
        opcodeMap.put(opcode.getIdentifier(), opcode);
        opcodeSet.add(opcode);

        return this;
    }

    public Opcode getOpcode(int opcode) {
        if (opcodeMap.containsKey(opcode)) {
            return opcodeMap.get(opcode);
        }

        return null;
    }

    public int opCount() {
        return opcodeSet.size();
    }
}
