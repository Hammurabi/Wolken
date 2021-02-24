package org.wokenproject.core;

import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.utils.FileService;

import java.io.IOException;

public class Context {
    private static Context instance;

    private Database            database;
    private NetworkParameters   networkParameters;
    private FileService         fileService;

    public Context(FileService service) throws WolkenException, IOException {
        this.database           = new Database(service.newFile("db"));
        this.networkParameters  = new NetworkParameters(false);
        this.fileService        = service;
    }

    public Database getDatabase()
    {
        return database;
    }

    public NetworkParameters getNetworkParameters()
    {
        return networkParameters;
    }

    public static Context getInstance()
    {
        return instance;
    }
}
