package com.example.threadpooldemo.pool;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * 队列执行
 */
public class SerialExecutor implements TaskBackgroundExecutor {

    private final ArrayDeque<Task> mTasks;
    private final Executor mExecutor;
    private final Object mLock;
    private volatile Runnable mActive;

    public SerialExecutor(@NonNull Executor executor) {
        mExecutor = executor;
        mTasks = new ArrayDeque<>();
        mLock = new Object();
    }


    @Override
    public Executor getDelegatedExecutor() {
        return mExecutor;
    }

    @Override
    public void execute(Runnable command) {
        synchronized (mLock) {
            if(null != command){
                Log.d("WorkTaskExecutor","SerialExecutor execute add Runnable:" + command);
            }
            mTasks.add(new Task(this, command));
            if (mActive == null) {
                scheduleNext();
            }
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

    private void scheduleNext() {
        synchronized (mLock) {
            if ((mActive = mTasks.poll()) != null) {
                mExecutor.execute(mActive);
            }
        }
    }

    public boolean hasPendingTasks() {
        synchronized (mLock) {
            return !mTasks.isEmpty();
        }
    }

    static class Task implements Runnable {
        final SerialExecutor mSerialExecutor;
        final Runnable mRunnable;

        Task(@NonNull SerialExecutor serialExecutor, @NonNull Runnable runnable) {
            mSerialExecutor = serialExecutor;
            mRunnable = runnable;
        }

        @Override
        public void run() {
            try {
                mRunnable.run();
            } catch (Exception exception){
                Log.e("WorkTaskExecutor","Task run() ,exception " + exception);
            } finally {
                mSerialExecutor.scheduleNext();
            }
        }
    }
}
