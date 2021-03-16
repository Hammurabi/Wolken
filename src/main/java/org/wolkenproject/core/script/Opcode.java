package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.InvalidTransactionException;
import org.wolkenproject.exceptions.MochaException;
import org.wolkenproject.utils.VoidCallableThrowsTY;

public class Opcode {
    private String          name;
    private String          desc;
    private String          usage;
    private int             identifier;
    private int             numArgs;
    private boolean         vararg;
    private VoidCallableThrowsTY<Scope, MochaException, InvalidTransactionException> callable;
    private long            weight;

    public Opcode(String name, String desc, String usage, int identifier, boolean vararg, int numArgs, VoidCallableThrowsTY<Scope, MochaException, InvalidTransactionException> callable, long weight) {
        this.name = name;
        this.desc = desc;
        this.usage= usage;
        this.vararg= vararg;
        this.numArgs= numArgs;
        this.callable= callable;
        this.identifier= identifier;
        this.weight = weight;
    }

    public void execute(Scope scope) throws MochaException, InvalidTransactionException {
        callable.call(scope);
    }

    public Opcode makeCopy() {
        return new Opcode(name, desc, usage, identifier, vararg, numArgs, callable);
    }

    protected void setIdentifier(int id) {
        this.identifier = id;
    }

    public String getName() {
        return name;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getDesc() {
        return desc;
    }

    public String getUsage() {
        return usage;
    }

    public boolean hasVarargs() {
        return vararg;
    }

    public int getNumArgs() {
        return numArgs;
    }

    public long getWeight() {
        return weight;
    }
}
