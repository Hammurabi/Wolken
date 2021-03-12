package org.wolkenproject.core.script;

import org.wolkenproject.core.script.opcodes.OpHalt;
import org.wolkenproject.core.script.opcodes.OpIConst_4bits;

public class Opcodes {
    public static final Opcode halt             = new OpHalt();
    public static final Opcode iconst_0         = new OpIConst_4bits(0);
}
