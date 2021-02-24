package org.wokenproject.core;

import org.wokenproject.exceptions.WolkenException;

public class Context {
    private static Context instance;

    private Database            database;
    private NetworkParameters   networkParameters;

    public Context() throws WolkenException {
        this.database           = new Database();
        this.networkParameters  = new NetworkParameters(false);
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
