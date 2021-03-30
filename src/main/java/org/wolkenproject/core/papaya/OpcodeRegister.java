package org.wolkenproject.core.papaya;

import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.UndefOpcodeException;
import org.wolkenproject.utils.Utils;
import org.wolkenproject.utils.VoidCallableThrowsTY;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public byte[] parse(String asm) throws PapayaException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String data[] = asm.replaceAll("\n", " ").replaceAll("\\s+", " ").split(" ");
        Iterator<String> iterator = Arrays.stream(data).iterator();

        while (iterator.hasNext()) {
            String opName   = iterator.next().replaceAll("^(Op)", "").toLowerCase();
            Opcode opcode   = getOpcode(opName);
            outputStream.write(opcode.getIdentifier());

            if (opcode.hasVarargs()) {
                if (!iterator.hasNext()) {
                    throw new PapayaException("Reached EOF but expected argument(s) for '" + opName + "'.");
                }

                byte array[]    = null;
                String argument = iterator.next();

                if (argument.matches("\\d+")) {     // base 10 number
                    long arg = Long.parseLong(argument);

                    if (Long.toString(arg).equals(argument)) {
                        if (arg <= 255) {
                            array = new byte[] { (byte) arg };
                        } else if (arg <= 65535) {
                            array = Utils.takeApartChar((char) arg);
                        } else if (arg <= 16777215) {
                            array = Utils.takeApartInt24(arg);
                        } else if (arg <= 4294967295L) {
                            array = Utils.takeApart(arg);
                        } else {
                            array = Utils.takeApartLong(arg);
                        }
                    } else {
                        array = new BigInteger(argument).toByteArray();
                    }
                } else if (Base58.isEncoded(argument)) {  // base 58 value
                    array = Base58.decode(argument);
                } else if (Base16.isEncoded(argument)) {  // base 16 value
                    array = Base16.decode(argument);
                } else {
                    throw new PapayaException("Unknown format format for string '" + argument + "'.");
                }

                byte length[] = null;

                switch (opcode.getNumArgs()) {
                    case 1:
                        if (array.length > 255) {
                            throw new PapayaException("Opcode '" + opName + "' takes maximum length argument(s) of '" + opcode.getNumArgs() + "'.");
                        }

                        length = new byte[] { (byte) array.length };
                        break;
                    case 2:
                        if (array.length > 65535) {
                            throw new PapayaException("Opcode '" + opName + "' takes maximum length argument(s) of '" + opcode.getNumArgs() + "'.");
                        }

                        length = Utils.takeApartChar((char) array.length);
                        break;
                    case 3:
                        if (array.length > 16777215) {
                            throw new PapayaException("Opcode '" + opName + "' takes maximum length argument(s) of '" + opcode.getNumArgs() + "'.");
                        }

                        length = Utils.takeApartInt24((char) array.length);
                        break;
                    case 4:
                        length = Utils.takeApart((char) array.length);
                        break;
                    default:
                        throw new PapayaException("Unsupported vararg size not in range of '1' to '4'.");
                }

                outputStream.write(length);
                outputStream.write(array);
            } else if (opcode.getNumArgs() > 0) {
                byte array[]    = null;
                String argument = iterator.next();

                if (argument.matches("\\d+")) {     // base 10 number
                    long arg = Long.parseLong(argument);

                    if (Long.toString(arg).equals(argument)) {
                        if (arg <= 255 && opcode.getNumArgs() == 1) {
                            array = new byte[] { (byte) arg };
                        } else if (arg <= 65535 && opcode.getNumArgs() == 2) {
                            array = Utils.takeApartChar((char) arg);
                        } else if (arg <= 16777215 && opcode.getNumArgs() == 3) {
                            array = Utils.takeApartInt24(arg);
                        } else if (arg <= 4294967295L && opcode.getNumArgs() == 4) {
                            array = Utils.takeApart(arg);
                        } else if (arg <= Long.MAX_VALUE && opcode.getNumArgs() == 8) {
                            array = Utils.takeApartLong(arg);
                        } else {
                            throw new PapayaException("Opcode '" + opName + "' takes '" + opcode.getNumArgs() + "' argument(s).");
                        }
                    } else {
                        array = new BigInteger(argument).toByteArray();
                    }
                } else if (Base58.isEncoded(argument)) {  // base 58 value
                    array = Base58.decode(argument);
                } else if (Base16.isEncoded(argument)) {  // base 16 value
                    array = Base16.decode(argument);
                } else {
                    throw new PapayaException("Unknown format format for string '" + argument + "'.");
                }

                if (array.length != opcode.getNumArgs()) {
                    throw new PapayaException("Opcode '" + opName + "' takes '" + opcode.getNumArgs() + "' argument(s).");
                }

                outputStream.write(array);
            }
        }

        outputStream.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    // register an opcode into the vm
    public OpcodeRegister registerOp(String name, String description, long weight, VoidCallableThrowsTY<Scope, PapayaException, InvalidTransactionException> callable) {
        return registerOp(name, description, 0, weight, callable);
    }

    public OpcodeRegister registerOp(String name, String description, int numArgs, long weight, VoidCallableThrowsTY<Scope, PapayaException, InvalidTransactionException> callable) {
        return registerOp(name, description, false, numArgs, weight, callable);
    }

    public OpcodeRegister registerOp(String name, String description, boolean vararg, int numArgs, long weight, VoidCallableThrowsTY<Scope, PapayaException, InvalidTransactionException> callable) {
        Opcode opcode = new Opcode(name, description, "", opcodeSet.size(), vararg, numArgs, callable, weight);
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
