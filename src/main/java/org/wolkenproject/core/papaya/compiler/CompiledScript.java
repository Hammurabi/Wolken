package org.wolkenproject.core.papaya.compiler;

import java.io.ByteArrayOutputStream;

public class CompiledScript {
    private final ByteArrayOutputStream compilationResult;
    private final PapayaApplication     application;

    public CompiledScript(PapayaApplication application) {
        this.application = application;
        this.compilationResult = new ByteArrayOutputStream();
    }
}
