package org.wolkenproject.core.script;

import org.wolkenproject.core.script.opcodes.OpHalt;
import org.wolkenproject.core.script.opcodes.OpIConst_4bits;
import org.wolkenproject.core.script.opcodes.OpPush;

public class Opcodes {
    public static final Opcode halt             = new OpHalt();
    public static final Opcode push             = new OpPush();

    public static final Opcode iconst0          = new OpIConst_4bits(0);
    public static final Opcode iconst1          = new OpIConst_4bits(1);
    public static final Opcode iconst2          = new OpIConst_4bits(2);
    public static final Opcode iconst3          = new OpIConst_4bits(3);
    public static final Opcode iconst4          = new OpIConst_4bits(4);
    public static final Opcode iconst5          = new OpIConst_4bits(5);
    public static final Opcode iconst6          = new OpIConst_4bits(6);
    public static final Opcode iconst7          = new OpIConst_4bits(7);
    public static final Opcode iconst8          = new OpIConst_4bits(8);
    public static final Opcode iconst9          = new OpIConst_4bits(9);
    public static final Opcode iconst10         = new OpIConst_4bits(10);
    public static final Opcode iconst11         = new OpIConst_4bits(11);
    public static final Opcode iconst12         = new OpIConst_4bits(12);
    public static final Opcode iconst13         = new OpIConst_4bits(13);
    public static final Opcode iconst14         = new OpIConst_4bits(14);
    public static final Opcode iconst15         = new OpIConst_4bits(15);

    public static final Opcode bconst           = new OpIConst_4bits(15);
    public static final Opcode sconst           = new OpIConst_4bits(15);
    public static final Opcode iconst           = new OpIConst_4bits(15);
    public static final Opcode lconst           = new OpIConst_4bits(15);
    public static final Opcode const128         = new OpIConst_4bits(15);
    public static final Opcode const256         = new OpIConst_4bits(15);

    public static final Opcode dup              = new OpDup(0);
    public static final Opcode dup2             = new OpDup(0);
    public static final Opcode dup4             = new OpDup(0);

    public static final Opcode rot              = new OpDup(0);
}
