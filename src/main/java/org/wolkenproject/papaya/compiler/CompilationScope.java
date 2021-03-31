package org.wolkenproject.papaya.compiler;

import java.io.OutputStream;

public class CompilationScope {
    private final OutputStream        stream;
    private final PapayaApplication   application;

    public CompilationScope(OutputStream stream, PapayaApplication application) {
        this.stream = stream;
        this.application = application;
    }

    public OutputStream getStream() {
        return stream;
    }

    public PapayaApplication getApplication() {
        return application;
    }
}
