package org.wolkenproject.papaya.runtime;

import org.wolkenproject.exceptions.PapayaException;

import java.util.Arrays;

public class PapayaObjectContainer implements PapayaContainer {
    private PapayaObject objects[];

    public PapayaObjectContainer(PapayaObject[] objects) {
        this.objects = objects;
    }

    @Override
    public PapayaObject getAtIndex(int index) throws PapayaException {
        if (index >= objects.length) {
            throw new PapayaException("array index out of '" + objects.length + "' bounds.");
        }

        return objects[index];
    }

    @Override
    public void setAtIndex(int index, PapayaHandler handler) throws PapayaException {
        if (index >= objects.length) {
            throw new PapayaException("array index out of '" + objects.length + "' bounds.");
        }

        objects[index] = handler.getPapayaObject();
    }

    @Override
    public void append(PapayaHandler object) {
        objects = Arrays.copyOf(objects, objects.length + 1);
        objects[objects.length - 1] = object.getPapayaObject();
    }
}
