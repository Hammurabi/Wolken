package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.VoidCallable;
import org.wolkenproject.utils.VoidCallableThrowsT;

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
    public OpcodeRegister registerOp(String name, String description, VoidCallableThrowsT<Scope, MochaException> callable) {
        return registerOp(name, description, 0, callable);
    }

    public OpcodeRegister registerOp(String name, String description, int numArgs, VoidCallableThrowsT<Scope, MochaException> callable) {
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
