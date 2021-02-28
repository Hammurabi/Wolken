package org.wolkenproject;

import org.apache.commons.cli.*;
import org.wolkenproject.core.Context;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.CryptoLib;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.HashUtil;

public class Wolken {
    public static void main(String args[]) throws ParseException {
        CryptoLib.initialize();

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        FileService mainDirectory   = new FileService();
        boolean     isTestNet       =;
        Context context = new Context(mainDirectory, isTestNet);
    }
}
