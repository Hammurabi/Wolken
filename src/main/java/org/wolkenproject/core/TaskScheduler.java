package org.wolkenproject.core;

import java.util.Queue;

public class TaskScheduler {
    private final Queue<Runnable> tasks;

    private final class Task {
        private long timeout;
        private Runnable task;

        private Task(Runnable task, long timeout) {
            this.task = task;
            this.timeout = timeout;
        }
    }
}
