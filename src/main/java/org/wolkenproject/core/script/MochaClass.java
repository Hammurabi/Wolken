package org.wolkenproject.core.script;

import org.wolkenproject.utils.Tuple;

import java.util.HashMap;
import java.util.Map;

public class MochaClass {
    private MochaClass                                  parent;
    private String                                      name;
    private Map<String, Tuple<Integer, MochaFunction>>  functions;

    public MochaClass(MochaClass parent) {
        this.parent = parent;
        functions   = new HashMap<>();
    }

    public void addFunction(String functionName, MochaFunction function) {
        functions.put(functionName, new Tuple<>(functions.size(), function));
    }

    public void populateFunctions(MochaFunction functions[]) {
        for (String string : this.functions.keySet()) {
            Tuple<Integer, MochaFunction> function = this.functions.get(string);
            functions[function.getFirst()] = function.getSecond();
        }
    }

    public void populateMembers(MochaObject[] members) {
    }

    // call any functions defined in this class by name
    public int getFunction(String function) {
        if (functions.containsKey(function)) {
            return functions.get(function).getFirst();
        }

        if (parent != null) {
            return parent.getFunction(function);
        }

        return -1;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFunctionCount() {
        int totalFunctions  = functions.size();

        if (parent != null) {
            totalFunctions += parent.getFunctionCount();
        }

        return totalFunctions;
    }

    public int getMemberCount() {
        int totalMembers    = functions.size();

        if (parent != null) {
            totalMembers    += parent.getMemberCount();
        }

        return totalMembers;
    }
}
