package com.example.threadpooldemo.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public interface TaskBackgroundExecutor {

    Executor getDelegatedExecutor();

    void execute(Runnable command);

    Future<?> submit(Runnable task);

    <T> Future<T> submit(Runnable task, T result);

    <T> Future<T> submit(Callable<T> task);
}
