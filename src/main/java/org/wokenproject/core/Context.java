package org.wokenproject.core;

import org.wokenproject.exceptions.WolkenException;

public class Context {
    private static Context instance;

    private NetworkParameters networkParameters;

    public Context() throws WolkenException {
        this.networkParameters = new NetworkParameters(false);
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
