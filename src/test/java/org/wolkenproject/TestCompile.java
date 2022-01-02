package org.wolkenproject;

import org.wolkenproject.core.ResourceManager;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.archive.PapayaArchive;
import org.wolkenproject.papaya.compiler.*;
import org.wolkenproject.papaya.compiler.Compiler;
import org.wolkenproject.papaya.runtime.OpcodeRegister;

import java.io.IOException;

public class TestCompile {
    public void testCompile() throws WolkenException, IOException, PapayaException {
        CryptoLib.getInstance();
        String program = ResourceManager.getString("/papaya/contract.pya");
        String librariesProgram = ResourceManager.getString("/papaya/libraries.pya");
        OpcodeRegister opcodeRegister = new OpcodeRegister();
        OpcodeRegister.register(opcodeRegister);
        Compiler compiler = new PapayaCompiler(opcodeRegister);
        PapayaArchive archive = compiler.createArchive(program, "-identifiers sequential");
        PapayaArchive libraries = compiler.createArchive(librariesProgram, "-identifiers sequential");
        PapayaApplication application = compiler.compile(archive, libraries, "-identifiers sequential");

        System.out.println(application.toString());
    }

    public static void main(String args[]) throws WolkenException, IOException, PapayaException {
        new TestCompile().testCompile();
    }
}
