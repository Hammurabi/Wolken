package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.exceptions.UndefOpcodeException;
import org.wolkenproject.utils.VoidCallable;
import org.wolkenproject.utils.VoidCallableThrowsT;
import org.wolkenproject.utils.VoidCallableThrowsTY;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class OpcodeRegister {
    private Map<String, Opcode>     opcodeNameMap;
    private Map<Integer, Opcode>    opcodeMap;
    private Set<Opcode>             opcodeSet;

    public OpcodeRegister() {
        opcodeNameMap = new HashMap<>();
        opcodeMap = new HashMap<>();
        opcodeSet = new LinkedHashSet<>();
    }

    public byte[] parse(String asm) throws MochaException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String data[] = asm.replaceAll("\n", " ").replaceAll("\\s+", " ").split(" ");
        Iterator<String> iterator = Arrays.stream(data).iterator();

        while (iterator.hasNext()) {
            String opName   = iterator.next().replaceAll("^(Op)", "").toLowerCase();
            Opcode opcode   = getOpcode(opName);

            if (opcode.hasVarargs()) {
                if (!iterator.hasNext()) {
                    throw new MochaException("Reached EOF but expected arguments for 'Op" + opName + "'.");
                }

                byte args[]     = null;
            } else if (opcode.getNumArgs() > 0) {
            }
        }

        return outputStream.toByteArray();
    }

    // register an opcode into the vm
    public OpcodeRegister registerOp(String name, String description, VoidCallableThrowsTY<Scope, MochaException, InvalidTransactionException> callable) {
        return registerOp(name, description, 0, callable);
    }

    public OpcodeRegister registerOp(String name, String description, int numArgs, VoidCallableThrowsTY<Scope, MochaException, InvalidTransactionException> callable) {
        return registerOp(name, description, false, numArgs, callable);
    }

    public OpcodeRegister registerOp(String name, String description, boolean vararg, int numArgs, VoidCallableThrowsTY<Scope, MochaException, InvalidTransactionException> callable) {
        Opcode opcode = new Opcode(name, description, "", opcodeSet.size(), vararg, numArgs, callable);
        opcodeNameMap.put(name, opcode);
        opcodeMap.put(opcode.getIdentifier(), opcode);
        opcodeSet.add(opcode);

        return this;
    }

    private Opcode getOpcode(String opName) throws UndefOpcodeException {
        if (opcodeNameMap.containsKey(opName)) {
            return opcodeNameMap.get(opName);
        }

        throw new UndefOpcodeException(opName);
    }

    public Opcode getOpcode(int opcode) throws UndefOpcodeException {
        if (opcodeMap.containsKey(opcode)) {
            return opcodeMap.get(opcode);
        }

        throw new UndefOpcodeException(opcode);
    }

    public int opCount() {
        return opcodeSet.size();
    }
}
