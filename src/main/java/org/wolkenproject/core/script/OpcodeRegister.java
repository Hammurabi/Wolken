package org.wolkenproject.core.script;

import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.exceptions.UndefOpcodeException;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VoidCallable;
import org.wolkenproject.utils.VoidCallableThrowsT;
import org.wolkenproject.utils.VoidCallableThrowsTY;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
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
                    throw new MochaException("Reached EOF but expected arguments for '" + opName + "'.");
                }

                byte array[]    = null;
                String argument = iterator.next();

                if (argument.matches("\\d+")) {     // base 10 number
                    array = new BigInteger(argument).toByteArray();
                } else if (Base58.isEncoded(argument)) {  // base 58 value
                    array = Base58.decode(argument);
                } else if (Base16.isEncoded(argument)) {  // base 16 value
                    array = Base16.decode(argument);
                } else if (argument.startsWith("'") && argument.endsWith("'")) {  // regular string
                    array = argument.getBytes();
                } else {
                    throw new MochaException("Unknown format format for string '" + argument + "'.");
                }

                byte length[] = null;

                switch (opcode.getNumArgs()) {
                    case 1:
                        if (array.length > 255) {
                            throw new MochaException("Opcode '" + opName + "' takes maximum arguments of '" + opcode.getNumArgs() + "'.");
                        }

                        length = new byte[] { (byte) array.length };
                        break;
                    case 2:
                        if (array.length > 65535) {
                            throw new MochaException("Opcode '" + opName + "' takes maximum arguments of '" + opcode.getNumArgs() + "'.");
                        }

                        length = Utils.takeApartChar((char) array.length);
                        break;
                    case 3:
                        if (array.length > 16777215) {
                            throw new MochaException("Opcode '" + opName + "' takes maximum arguments of '" + opcode.getNumArgs() + "'.");
                        }

                        length = Utils.takeApartInt24((char) array.length);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    default:
                        throw new MochaException("Unsupported vararg size larger than '8'.");
                }

                if (array.length > maxArgs) {
                    throw new MochaException("Opcode '" + opName + "' takes maximum arguments of '" + array.length + "'.");
                }
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
