package org.wolkenproject.core.script;

public abstract class Opcode {
    private int identifier;
    private BitFields args;
    private String    desc;

    protected void setIdentifier(int id) {
        this.identifier = id;
    }

    protected void setArgs(BitFields args) {
        this.args = args;
    }

    protected void setDescription(String desc) {
        this.desc = desc;
    }

    public abstract void execute(VirtualProcess virtualProcess);

    public int getIdentifier() {
        return identifier;
    }

    public BitFields getArgs() {
        return args;
    }

    public String getDesc() {
        return desc;
    }

//    @Override
//    public void write(OutputStream stream) throws IOException, WolkenException {
//    }
//
//    @Override
//    public void read(InputStream stream) throws IOException, WolkenException {
//    }
//
//    @Override
//    public <Type extends SerializableI> Type newInstance(Object... object) throws WolkenException {
//        return null;
//    }
//
//    @Override
//    public int getSerialNumber() {
//        return Context.getInstance().getSerialFactory().getSerialNumber(Opcode.class);
//    }
}
