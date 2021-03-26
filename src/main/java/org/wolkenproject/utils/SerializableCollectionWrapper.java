package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.util.Collection;

public class SerializableCollectionWrapper<T> extends SerializableI {
    private Collection<T> collection;

    public SerializableCollectionWrapper(Collection<T> collection) {
    }
}
