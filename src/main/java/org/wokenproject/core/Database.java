package org.wokenproject.core;

import org.iq80.leveldb.DB;
import org.wokenproject.utils.FileService;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

public class Database {
    private DB db;

    public Database(FileService location)
    {
        db = Iq80DBFactory.factory.open(location.file(), new Options());
    }
}
