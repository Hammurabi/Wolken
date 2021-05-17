package org.wolkenproject.papaya.compiler;

import org.wolkenproject.papaya.archive.ArchivedMethod;
import org.wolkenproject.papaya.archive.ArchivedStructureI;
import org.wolkenproject.papaya.archive.PapayaArchive;
import org.wolkenproject.papaya.runtime.OpcodeRegister;

import java.util.Map;
import java.util.Stack;

public class CompilationScope {
    private final Stack<ArchivedStructureI> classTrace;
    private final Stack<FunctionScope>      functionTrace;
    private final PapayaArchive             archive;
    private final OpcodeRegister            opcodeRegister;
    private final Map<String, Traverser>    traverserMap;

    public CompilationScope(PapayaArchive archive, OpcodeRegister opcodeRegister, Map<String, Traverser> traverserMap) {
        classTrace = new Stack<>();
        functionTrace = new Stack<>();
        this.archive = archive;
        this.opcodeRegister = opcodeRegister;
        this.traverserMap = traverserMap;
    }

    public void enterClass(ArchivedStructureI structure) {
        classTrace.push(structure);
    }

    public FunctionScope enterFunction(ArchivedMethod method) {
        FunctionScope scope = null;
        functionTrace.push(scope = new FunctionScope(method, classTrace.peek(), this));

        return scope;
    }

    public void exitClass() {
        classTrace.pop();
    }

    public byte[] exitFunction() {
        return functionTrace.pop().getOutputStream().toByteArray();
    }

    public OpcodeRegister getOpcodeRegister() {
        return opcodeRegister;
    }

    public PapayaArchive getArchive() {
        return archive;
    }

    public Map<String, Traverser> getTraverserMap() {
        return traverserMap;
    }
}
