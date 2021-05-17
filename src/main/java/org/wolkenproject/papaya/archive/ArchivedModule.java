package org.wolkenproject.papaya.archive;

import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.papaya.compiler.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArchivedModule implements ArchivedStructureI {
    private final String                            name;
    private final Map<String, ArchivedModule>       modules;
    private final Map<String, ArchivedMember>       members;
    private final Map<String, ArchivedMethod>       methods;
    private final Map<String, ArchivedStruct>       structs;
    private final LineInfo                          lineInfo;

    public ArchivedModule(String name) {
        this(name, new LineInfo(-1, -1));
    }

    public ArchivedModule(String name, LineInfo lineInfo) {
        this.name    = name;
        this.modules = new HashMap<>();
        this.members = new HashMap<>();
        this.methods = new HashMap<>();
        this.structs = new HashMap<>();
        this.lineInfo = lineInfo;
    }

    public void declare(String name, ArchivedMember field) throws PapayaException {
        if (containsName(name)) {
            throw new PapayaException("redeclaration of '" + name + "' at " + field.getLineInfo());
        }
        
        members.put(name, field);
    }

    public void declare(String name, ArchivedMethod function) throws PapayaException {
        if (containsName(name)) {
            throw new PapayaException("redeclaration of '" + name + "' at " + function.getLineInfo());
        }
        
        methods.put(name, function);
    }

    public void declare(String name, ArchivedModule module) throws PapayaException {
        if (containsName(name)) {
            throw new PapayaException("redeclaration of '" + name + "' at " + module.getLineInfo());
        }

        modules.put(name, module);
    }

    public void declare(String name, ArchivedStruct struct) throws PapayaException {
        if (containsName(name)) {
            throw new PapayaException("redeclaration of '" + name + "' at " + struct.getLineInfo());
        }

        structs.put(name, struct);
    }

    @Override
    public void declare(String name, ArchivedStructureI structure) throws PapayaException {
        if (structure instanceof ArchivedModule) {
            declare(name, (ArchivedModule) structure);
        } else if (structure instanceof ArchivedStruct) {
            declare(name, (ArchivedStruct) structure);
        } else {
            throw new PapayaException("invalid type '" + structure.getClass().getName() + "' provided as structure.");
        }
    }

    public boolean containsName(String name) {
        return
                modules.containsKey(name) ||
                members.containsKey(name) ||
                methods.containsKey(name) ||
                structs.containsKey(name);
    }

    public LineInfo getLineInfo() {
        return lineInfo;
    }

    public String getName() {
        return name;
    }

    public String formattedString(int i) {
        StringBuilder builder = new StringBuilder();
        StringBuilder tabs = new StringBuilder();
        for (int t = 0; t < i; t ++) {
            tabs.append("\t");
        }

        builder.append(tabs).append("module").append(" ").append(name).append("\n");

        for (String module : modules.keySet()) {
            builder.append(tabs).append(modules.get(module).formattedString(i + 1));
        }

        for (String struct : structs.keySet()) {
            builder.append(tabs).append(structs.get(struct).formattedString(i + 1));
        }

        for (String member : members.keySet()) {
            builder.append(tabs).append(members.get(member).formattedString(i + 1));
        }

        for (String method : methods.keySet()) {
            builder.append(tabs).append(methods.get(method).formattedString(i + 1));
        }

        return builder.toString();
    }

    public String formattedString() {
        return formattedString(0);
    }

    public List<PapayaStructure> getStructures() {
        List<PapayaStructure> structureList = new ArrayList<>();
        for (ArchivedStruct struct : structs.values()) {
            PapayaStructure structure = new PapayaStructure(
                    struct.getName(),
                    struct.getType(),
                    struct.getLineInfo()
            );

            structureList.add(structure);
        }

        return null;
    }

    public ArchivedMethod getEntryPoint() {
        return null;
    }

    public ArchivedStructureI getModuleOrStruct(String name) {
        if (modules.containsKey(name)) return modules.get(name);
        else if (structs.containsKey(name)) return structs.get(name);

        return null;
    }

    @Override
    public boolean containsMember(String ident) {
        return members.containsKey(ident) || methods.containsKey(ident);
    }

    @Override
    public ArchivedMember getMember(String ident) {
        return members.get(ident);
    }

    public void compile(PapayaApplication application, CompilationScope compilationScope) throws PapayaException {
        PapayaStructure structure = new PapayaStructure(name, StructureType.ModuleType, lineInfo);
        compilationScope.enterClass(this);

        for (ArchivedModule module : modules.values()) {
            module.compile(application, compilationScope);
        }

        for (ArchivedStruct struct : structs.values()) {
            struct.compile(application, compilationScope);
        }

        for (ArchivedMember member : members.values()) {
            member.compile(application, compilationScope);
        }

        for (ArchivedMethod method : methods.values()) {
            method.compile(compilationScope);
        }
    }
}
