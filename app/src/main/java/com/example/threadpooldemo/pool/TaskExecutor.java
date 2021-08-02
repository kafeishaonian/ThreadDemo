package com.example.threadpooldemo.pool;

import androidx.annotation.RestrictTo;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface TaskExecutor {

    void postToMainThread(Runnable runnable);

    void postToMainThreadDelayed(Runnable runnable,long delayMillis);

    void removeMainThreadCallbacks(Runnable runnable);

    Executor getMainThreadExecutor();

    Future<?> executeOnBackgroundThread(Runnable runnable);

    <T> Future<T> executeOnBackgroundThread(Callable<T> callable);

    TaskBackgroundExecutor getBackgroundExecutor();

}
