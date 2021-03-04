package org.wolkenproject.utils;

import org.wolkenproject.core.Context;
import org.wolkenproject.exceptions.WolkenException;
import org.wolkenproject.serialization.SerializableI;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SolidStateCollection<T extends SerializableI> {
    private List<T>     list;
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

    public void add(T element) throws FileNotFoundException {
        list.add(element);
        size ++;

        if (list.size() > maxItemsInRam) {
            unload();
        }
    }

    private void unload() throws IOException {
        int chunkIndex = size / maxItemsInRam;
        OutputStream outputStream = new FileOutputStream(service.newFile("cnk_" + chunkIndex).file());
        outputStream.flush();
        outputStream.close();
    }

    Iterator<T> getIterator() throws IOException, WolkenException {
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

    private T[] loadChunk(int index) throws IOException, WolkenException {
        InputStream inputStream = new FileInputStream(service.newFile("cnk_" + index).file());
        T[] chunk = (T[]) new Object[maxItemsInRam];
        for (int i = 0; i < chunk.length; i ++) {
            chunk[i] = Context.getInstance().getSerialFactory().fromStream(inputStream);
        }

        inputStream.close();
        return chunk;
    }
}
