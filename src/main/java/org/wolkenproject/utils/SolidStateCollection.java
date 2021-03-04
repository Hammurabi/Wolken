package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

public class SolidStateCollection<T extends SerializableI> {
    private int         maxItemsInRam;
    private int         size;
    private FileService service;

    public SolidStateCollection(int maxItemsInRam, FileService service) {
        this.maxItemsInRam  = maxItemsInRam;
        this.size           = 0;
        this.service        = service;
    }

//    public T getItem(int index) {
//        int blockIndex      = index / maxItemsInRam;
//        int currentBlock    = size / maxItemsInRam;
//
//        if (currentBlock == blockIndex) {
//            fetchItemFromRam(blockIndex, blockIndex % index);
//        }
//
//        return loadItemExclusively(index);
//    }

    Iterator<T> getIterator() throws FileNotFoundException {
        return new Iterator<T>() {
            int index = 0;
            T[] chunk = loadChunk(0);

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }
}
