package org.wokenproject.core;

import org.wokenproject.exceptions.WolkenException;
import org.wokenproject.utils.FileService;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Context {
    private static Context instance;

    private Database            database;
    private NetworkParameters   networkParameters;
    private ExecutorService     threadPool;
    private AtomicBoolean       isRunning;
    private FileService         fileService;


    public Context(FileService service, boolean testNet) throws WolkenException, IOException {
        this.database           = new Database(service.newFile("db"));
        this.networkParameters  = new NetworkParameters(testNet);
        this.threadPool         = Executors.newFixedThreadPool(3);
        this.isRunning          = new AtomicBoolean(true);
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

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public boolean isRunning() {
        return isRunning.get();
    }
}
