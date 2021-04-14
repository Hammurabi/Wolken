package org.wolkenproject.core;

import org.wolkenproject.utils.VoidCallable;

import java.util.HashSet;
import java.util.Set;

public class Emitter<T> {
    private final Set<VoidCallable<T>> listeners;

    public Emitter() {
        this.listeners = new HashSet<>();
    }

    public void call(T t) {
        listeners.forEach(listener -> call(t));
    }

    public void add(VoidCallable<T> listener) {
        listeners.add(listener);
    }
}
