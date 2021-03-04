package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SolidStateCollection<T extends SerializableI> {
    private List<T> list;
    private int         maxItemsInRam;
    private int         size;
    private FileService service;

    public SolidStateCollection(int maxItemsInRam, FileService service) {
        this.maxItemsInRam  = maxItemsInRam;
        this.size           = 0;
        this.service        = service;
        this.list           = new ArrayList<>();
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

    public void add(T element) {

    }

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

    private T[] loadChunk(int index) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(service.newFile("cnk_" + index).file());

    }
}
