package org.wolkenproject.core.script;

import java.util.Stack;

public class MochaStack<T> {
    private Stack<T> stack;

    public MochaStack() {
        stack = new Stack<>();
    }

    public void push(T element) {
        stack.push(element);
    }

    public T peek() {
        return stack.peek();
    }

    public T pop() {
        return stack.pop();
    }

    public void dup() {
        stack.push(stack.peek());
    }

    public MochaStack<T> dupr() {
        stack.push(stack.peek());
        return this;
    }

    public void dup(int element) {
        stack.push(stack.get(stack.size() - element));
    }

    public void rot(int index) {
    }

    public void swap(int a, int b) {
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
