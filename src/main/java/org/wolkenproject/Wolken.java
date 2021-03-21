package org.wolkenproject;

import org.apache.commons.cli.*;
import org.wolkenproject.core.*;
import org.wolkenproject.core.transactions.Transaction;
import org.wolkenproject.crypto.ec.ECKeypair;
import org.wolkenproject.encoders.Base16;
import org.wolkenproject.encoders.Base58;
import org.wolkenproject.crypto.CryptoLib;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.network.NetAddress;
import org.wolkenproject.utils.FileService;
import org.wolkenproject.utils.Logger;
import org.wolkenproject.wallet.BasicWallet;

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
        //-quicksend to amount fee wallet pass
        options.addOption("quick_sign", true, "quickly make a transaction and sign it.");
        options.addOption("broadcast_tx", true, "broadcast a transaction to the network.");

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

        if (cmd.hasOption("quick_sign")) {
            String qsArgs[] = cmd.getOptionValues("quick_sign");
            if (qsArgs.length != 5) {
                throw new WolkenException("quicksend expects 5 arguments, '"+qsArgs.length+"' provided.");
            }

            BasicWallet wallet = new BasicWallet(mainDirectory.newFile(qsArgs[3]));

            if (!Address.isValidAddress(Base58.decode(qsArgs[0]))) {
                throw new WolkenException("address '" + qsArgs[0] + "' is invalid.");
            }

            long amount = Long.parseLong(qsArgs[1]);
            long fee    = Long.parseLong(qsArgs[2]);

            Address recipient = Address.fromFormatted(Base58.decode(qsArgs[0]));
            Transaction transaction = Transaction.newTransfer(recipient, amount, fee, wallet.getNonce() + 1);
            transaction = transaction.sign(new ECKeypair(wallet.getPrivateKey(qsArgs[4].toCharArray())));

            Logger.alert("transaction signed successfully ${t}", Base16.encode(transaction.asSerializedArray()));
            System.exit(0);
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

        int rpcPort = 5420;
        Context context = new Context(mainDirectory, rpcPort, isTestNet, address, connectionList);
    }
}
