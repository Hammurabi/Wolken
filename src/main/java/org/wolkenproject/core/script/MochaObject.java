package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.UndefClassException;
import org.wolkenproject.exceptions.UndefFunctionException;
import org.wolkenproject.exceptions.UndefMemberException;

public class MochaObject {
    public static final char        DefaultMetadata = 0;

    private MochaClass              mochaClass;
    private MochaFunction           functions[];
    private MochaObject             members[];

    public MochaObject(VirtualProcess virtualProcess) throws UndefClassException {
        this(virtualProcess, DefaultMetadata);
    }

    public MochaObject(VirtualProcess virtualProcess, int metadata) throws UndefClassException {
        this(virtualProcess, virtualProcess.getClassProvider().getClass(metadata));
    }

    public MochaObject(VirtualProcess virtualProcess, MochaClass mochaClass) {
        this.mochaClass = mochaClass;
        this.functions  = new MochaFunction[mochaClass.getFunctionCount()];
        this.members    = new MochaObject[mochaClass.getMemberCount()];

        mochaClass.populateFunctions(functions);
        mochaClass.populateMembers(virtualProcess, members);
    }

    public MochaFunction getFunction(int functionId) throws UndefFunctionException {
        if (functionId >= functions.length) {
            throw new UndefFunctionException("no function '" + functionId + "' found.");
        }

        return functions[functionId];
    }

    public MochaObject getMember(int memberId) throws UndefMemberException {
        if (memberId >= members.length) {
            throw new UndefMemberException("no member '" + memberId + "' found.");
        }

        return members[memberId];
    }

    public MochaClass getMochaClass() {
        return mochaClass;
    }

    public void setMember(int memberId, MochaObject object) throws UndefMemberException {
        if (memberId >= members.length) {
            throw new UndefMemberException("no member '" + memberId + "' found.");
        }

        members[memberId] = object;
    }
}
