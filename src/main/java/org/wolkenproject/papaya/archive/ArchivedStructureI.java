package org.wolkenproject.papaya.archive;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.LineInfo;

public interface ArchivedStructureI {
    public void declare(String name, ArchivedMember field) throws PapayaException;
    public void declare(String name, ArchivedMethod function) throws PapayaException;
    public void declare(String name, ArchivedModule module) throws PapayaException;
    public void declare(String name, ArchivedStruct struct) throws PapayaException;
    public void declare(String name, ArchivedStructureI structure) throws PapayaException;
    public boolean containsName(String name);

    LineInfo getLineInfo();
    ArchivedStructureI getModuleOrStruct(String name);

    boolean containsMember(String ident);
    ArchivedMember getMember(String ident);
}
