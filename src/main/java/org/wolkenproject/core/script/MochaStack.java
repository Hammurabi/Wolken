package org.wolkenproject.core.script;

import org.wolkenproject.exceptions.MochaException;

import java.util.Stack;

public class MochaStack<T> {
    private Stack<T> stack;

    public MochaStack() {
        stack = new Stack<>();
    }

    public void push(T element) {
        stack.push(element);
    }

    public T peek() throws MochaException {
        if (isEmpty()) {
            throw new MochaException("EmptyStackException caught");
        }

        return stack.peek();
    }

    public T pop() throws MochaException {
        if (isEmpty()) {
            throw new MochaException("EmptyStackException caught");
        }

        return stack.pop();
    }

    public void dup() throws MochaException {
        if (isEmpty()) {
            throw new MochaException("EmptyStackException caught");
        }

        stack.push(stack.peek());
    }

    public void dup(int element) throws MochaException {
        int index = stack.size() - element;

        if (index < 0) {
            throw new MochaException("EmptyStackException caught");
        }

        stack.push(stack.get(stack.size() - element));
    }

    public MochaStack<T> rot() throws MochaException {
        swap(1, 2);

        return this;
    }

    public void rot(int index) {
    }

    public void swap(int i, int j) throws MochaException {
        int a = stack.size() - i;
        int b = stack.size() - j;

        if (a < 0 || b < 0) {
            throw new MochaException("EmptyStackException caught");
        }

        T temp = stack.get(stack.size() - a);
        stack.set(stack.size() - a, stack.get(stack.size() - b));
        stack.set(stack.size() - b, temp);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int getSize() {
        return stack.size();
    }
}
