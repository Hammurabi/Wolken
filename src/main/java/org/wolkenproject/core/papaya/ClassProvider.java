package org.wolkenproject.core.papaya;

public class ClassProvider {
//    Map<Integer, MochaClass> classMap;
//
//    public ClassProvider() {
//        classMap = new HashMap<>();
//
//        setDefaultMochaClass();
//    }
//
//    public void registerClass(MochaClass mochaClass) {
//        classMap.put(classMap.size(), mochaClass);
//    }
//
//    public MochaClass getClass(int metadata) throws UndefClassException {
//        if (!classMap.containsKey(metadata)) {
//            throw new UndefClassException("no metadata found for '" + metadata + "'.");
//        }
//
//        return classMap.get(metadata);
//    }
//
//    public MochaClass getDefaultMochaClass() throws UndefClassException {
//        return getClass(0);
//    }
//
//    public MochaClass getArrayMochaClass() throws UndefClassException {
//        return getClass(1);
//    }
//
//    private MochaClass getIntegerMochaClass() throws UndefClassException {
//        return getClass(2);
//    }
//
//    private void setDefaultMochaClass() {
//        MochaClass defaultClass = new MochaClass(null);
//        defaultClass.setName("Object");
//        // (self)
//        defaultClass.addFunction("hashCode", (proc)->{ return proc.getClassProvider().getArrayMochaClass().newInstanceNative(proc, proc.getMemoryModule().getStack().pop().getHash()); });
//        defaultClass.addFunction("toString", (proc)->{ return null; });
//        defaultClass.addFunction("getClassName", (proc)->{ return null; });
//
//        // (self, other)
//        defaultClass.addFunction("add", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("sub", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("mul", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("div", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("mod", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("shiftL", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("shiftR", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("arithmeticShift", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("and", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("or", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("not", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("xor", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("pow", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//
//        // (self)
//        defaultClass.addFunction("negate", (proc)->{ return new MochaObject(proc.getMemoryModule().getStack().pop()); });
//        defaultClass.addFunction("true", (proc)->{ return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("false", (proc)->{ return proc.getMemoryModule().getStack().pop(); });
//
//        registerClass(defaultClass);
//
//        MochaClass arrayClass = new MochaClass(defaultClass);
//        arrayClass.setName("Array");
//        arrayClass.addFunction("append", (proc)->{ MochaObject append = proc.getMemoryModule().getStack().pop(); MochaObject self = proc.getMemoryModule().getStack().pop(); if (!append.equals(self)) { append.append(append); return null; } throw new MochaException("cannot append self to array."); });
//        arrayClass.addFunction("pop", (proc)->{ proc.getMemoryModule().getStack().pop().pop(); return null;});
//        arrayClass.addFunction("len", (proc)->{ return proc.getClassProvider().getIntegerMochaClass().newInstanceNative(proc, proc.getMemoryModule().getStack().pop().getLength()); });
//        arrayClass.addFunction("reshape", (proc)->{ MochaObject shape = proc.getMemoryModule().getStack().pop(); proc.getMemoryModule().getStack().pop().reshape(shape); return null; });
//        arrayClass.addFunction("shape", (proc)->{ return proc.getMemoryModule().getStack().pop().getShape(); });
//        registerClass(arrayClass);
//
//        MochaClass abstractNumberClass = new MochaClass(defaultClass);
//        abstractNumberClass.setName("AbstractNumber");
//        abstractNumberClass.addMember("signed");
//        registerClass(abstractNumberClass);
//
//        MochaClass integerClass = new MochaClass(abstractNumberClass);
//        integerClass.setName("SignedInt");
//
//        // (self, other)
//        defaultClass.addFunction("add", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("sub", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("mul", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("div", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("mod", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("shiftL", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("shiftR", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("arithmeticShift", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("and", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("or", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("not", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("xor", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//        defaultClass.addFunction("pow", (proc)->{ proc.getMemoryModule().getStack().pop(); return proc.getMemoryModule().getStack().pop(); });
//    }
}
