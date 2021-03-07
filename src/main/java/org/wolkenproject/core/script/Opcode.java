package org.wolkenproject.core.script;

public class Opcode {
    private int             identifier;
    private BitFields       args;
    private String          desc;
    private OpcodeExecutor  executor;

    public void execute(VirtualProcess virtualProcess) {
        executor.execute(virtualProcess);
    }

    protected void setIdentifier(int id) {
        this.identifier = id;
    }

    protected void setArgs(BitFields args) {
        this.args = args;
    }

    protected void setDescription(String desc) {
        this.desc = desc;
    }

    public int getIdentifier() {
        return identifier;
    }

    public BitFields getArgs() {
        return args;
    }

    public String getDesc() {
        return desc;
    }

    public void setExecutor(OpcodeExecutor executor) {
        this.executor = executor;
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
