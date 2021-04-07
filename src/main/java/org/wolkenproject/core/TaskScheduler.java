package org.wolkenproject.core;

import java.util.LinkedList;
import java.util.Queue;

public class TaskScheduler implements Runnable {
    private final Queue<Runnable> tasks;

    public TaskScheduler() {
        tasks = new LinkedList<>();
    }

    @Override
    public void run() {
        while (Context.getInstance().isRunning()) {
            if (!tasks.isEmpty()) {
                tasks.poll().run();
            }
        }
    }

    public void runAsync(Runnable runnable) {
    }
}
