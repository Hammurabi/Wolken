package org.wolkenproject;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.wolkenproject.exceptions.PapayaException;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.papaya.compiler.Compiler;
import org.wolkenproject.papaya.compiler.PapayaApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestCompile {
    @Test
    public void testCompile() throws WolkenException, IOException, PapayaException {
        Compiler compiler = Compiler.getFor("papaya");
        StringBuilder program = new StringBuilder();
        String line = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/papaya/contract.pya")));
        while ((line = reader.readLine()) != null) {
            program.append(line).append("\n");
        }
        reader.close();

        PapayaApplication application = compiler.compile(program.toString(), new JSONObject());
        System.out.println(application);
    }
}
