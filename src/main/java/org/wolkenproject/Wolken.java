package org.wolkenproject;

import org.apache.commons.cli.*;
import org.wolkenproject.core.*;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.NetAddress;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class Wolken {
    public static void main(String args[]) throws ParseException, WolkenException, IOException {
        CryptoLib.getInstance();

        Options options = new Options();
        options.addOption("dir", true, "set the main directory for wolken, otherwise uses the default application directory of the system.");
        options.addOption("enable_fullnode", true, "set the node to a full node.");
        options.addOption("enable_testnet", true, "set the testnet to enabled/disabled.");
        options.addOption("enable_mining", true, "set the node to a mining node.");
        options.addOption("enable_storage", true, "act as a storage node.");
        options.addOption("enable_seeding", false, "act as a seeding node.");
        options.addOption("force_connect", true, "force a connection to an array of {ip:port}.");
        //-quicksend {to:xxxxxx,amount:12,fee:0.00001,wallet:xxx,pass:yyy}
        options.addOption("quicksend", true, "quickly make a transaction and sign it.");

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
            // we could parse into a boolean here
            if (value.equals("true")) {
                isTestNet = true;
            } else if (value.equals("false")) {
                isTestNet = false;
            } else {
                Logger.faterr("provided argument '-enable_testnet " + cmd.getOptionValue("enable_testnet") + "' is invalid.");
                return;
            }
        }

        mainDirectory = mainDirectory.newFile("Wolken");
        if (!mainDirectory.exists())
        {
            mainDirectory.makeDirectory();
        }

        Address address[] = null;

        if (cmd.hasOption("enable_mining")) {
            String value = cmd.getOptionValue("enable_mining").toLowerCase();
            value = value.substring(1, value.length() - 1);

            String addresses[] = value.split(",");
            address = new Address[addresses.length];

            int i = 0;
            for (String b58 : addresses) {
                byte bytes[] = Base58.decode(b58);

                if (!Address.isValidAddress(bytes)) {
                    throw new WolkenException("invalid address '" + b58 + "' provided.");
                }

                address[i ++] = Address.fromFormatted(bytes);
            }
        }

        Set<NetAddress> connectionList = new HashSet<>();

        if (cmd.hasOption("force_connect")) {
            String value = cmd.getOptionValue("force_connect");
            String ips[] = value.substring(1, value.length() - 1).split(",");
            for (String ipInfo : ips) {
                String ip[] = ipInfo.split(":");
                connectionList.add(new NetAddress(InetAddress.getByName(ip[0]), Integer.parseInt(ip[1]), 0));
            }
        }

        Logger.alert("force connections ${l}", connectionList);

        Context context = new Context(mainDirectory, isTestNet, address, connectionList);
    }
}
