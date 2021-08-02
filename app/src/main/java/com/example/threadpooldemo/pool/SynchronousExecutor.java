package com.example.threadpooldemo.pool;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class SynchronousExecutor implements TaskBackgroundExecutor{

    private final Executor executor;
    public SynchronousExecutor(Executor executor){
        this.executor = executor;
    }

    @Override
    public Executor getDelegatedExecutor() {
        return executor;
    }

    @Override
    public void execute(Runnable command) {
        if(null != executor){
            if(null != command){
                Log.d("WorkTaskExecutor","SynchronousExecutor execute add Runnable:" + command);
            }
            executor.execute(command);
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        Future future = null;
        if(task instanceof RunnableFuture) {
            future = (RunnableFuture) task;
            execute(task);
        }else {
            future = submit(task,null);
        }
        return future;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        RunnableFuture<T> runnableFuture = new FutureTask<T>(task, result){
            @NonNull
            @Override
            public String toString() {
                if(task != null) {
                    return task.getClass().getName() + "@" + Integer.toHexString(task.hashCode());
                }else {
                    return super.toString();
                }
            }
        };
        execute(runnableFuture);
        return runnableFuture;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        RunnableFuture<T> runnableFuture = new FutureTask<T>(task){
            @NonNull
            @Override
            public String toString() {
                if(task != null) {
                    return task.getClass().getName() + "@" + Integer.toHexString(task.hashCode());
                }else {
                    return super.toString();
                }
            }
        };
        execute(runnableFuture);
        return runnableFuture;
    }
}
