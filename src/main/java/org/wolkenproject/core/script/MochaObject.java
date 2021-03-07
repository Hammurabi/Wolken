package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;
import org.wolkenproject.exceptions.UndefFunctionException;

public class MochaObject {
    public static final char        DefaultMetadata = 0;

    private MochaClass              mochaClass;
    private MochaFunction           functions[];
    private MochaObject             members[];

    public MochaObject(VirtualMachine virtualMachine) throws UndefClassException {
        this(virtualMachine, DefaultMetadata);
    }

    public MochaObject(VirtualMachine virtualMachine, char metadata) throws UndefClassException {
        this(virtualMachine.getClassProvider().getClass(metadata));
    }

    public MochaObject(MochaClass mochaClass) {
        this.mochaClass = mochaClass;
        this.functions  = new MochaFunction[mochaClass.getFunctionCount()];
        this.members    = new MochaObject[mochaClass.getMemberCount()];

        mochaClass.populateFunctions(functions);
        mochaClass.populateMembers(members);
    }

    public MochaFunction getFunction(int functionId) throws UndefFunctionException {
        if (functionId >= functions.length) {
            throw new UndefFunctionException("no function '" + functionId + "' found.");
        }

        return functions[functionId];
    }

    public MochaObject getMember(int memberId) throws UndefClassException {
        if (memberId >= members.length) {
            throw new UndefClassException("no member '" + memberId + "' found.");
        }

        return members[memberId];
    }

    public MochaClass getMochaClass() {
        return mochaClass;
    }
}
