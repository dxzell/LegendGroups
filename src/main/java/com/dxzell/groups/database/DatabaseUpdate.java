package com.dxzell.groups.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class DatabaseUpdate {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public static void runAsync(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }

    public static void stopExecutorService() {
        EXECUTOR_SERVICE.shutdown();
    }
}
