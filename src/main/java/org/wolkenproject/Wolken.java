package org.wolkenproject;

import org.apache.commons.cli.*;
import org.wolkenproject.core.Context;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.CryptoLib;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.HashUtil;
import org.wolkenproject.utils.Logger;

import java.io.IOException;

public class Wolken {
    public static void main(String args[]) throws ParseException, WolkenException, IOException {
        CryptoLib.initialize();

        Options options = new Options();
        options.addOption("dir", true, "set the main directory for wolken, otherwise uses the default application directory of the system.");
        options.addOption("enable_testnet", true, "set the testnet to enabled/disabled.");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        FileService mainDirectory = FileService.appDir();
        if (cmd.hasOption("dir")) {
            FileService dir = new FileService(cmd.getOptionValue("dir"));
            if (dir.exists()) {
                mainDirectory = dir;
            } else {
                Logger.faterr("provided directory '" + cmd.getOptionValue("dir") + "' does not exist.");
                return;
            }
        }

        boolean isTestNet = false;
        if (cmd.hasOption("enable_testnet")) {
            String value = cmd.getOptionValue("enable_testnet").toLowerCase();
            if (value.equals("true")) {
                isTestNet = true;
            } else if (value.equals("false")) {
                isTestNet = false;
            } else {
                Logger.faterr("provided argument '-enable_testnet " + cmd.getOptionValue("enable_testnet") + "' is invalid.");
                return;
            }
        }
        Context context = new Context(mainDirectory, isTestNet);
    }
}
