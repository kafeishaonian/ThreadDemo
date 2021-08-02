package com.example.threadpooldemo.pool;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class WorkTaskExecutor implements TaskExecutor {
    public static final String TAG = WorkTaskExecutor.class.getSimpleName();
    public static final String DEFAULT_WORK_THREAD_POOL_NAME = "Default";
    private TaskBackgroundExecutor backgroundExecutor;
    private static final ConcurrentMap<String, WorkTaskExecutor> workTaskExecutors = new ConcurrentHashMap<>();
    private WorkTaskExecutor(String key) {
        setBackgroundExecutor(key);
    }

    public static WorkTaskExecutor get() {
        return get(DEFAULT_WORK_THREAD_POOL_NAME);
    }

    public static synchronized WorkTaskExecutor get(String key){
        WorkTaskExecutor executor = workTaskExecutors.get(key);
        if (executor == null){
            executor = new WorkTaskExecutor(key);
            workTaskExecutors.put(key, executor);
        }
        return executor;
    }

    public synchronized WorkTaskExecutor setBackgroundExecutor(String name){
        Executor executor = createDefaultExecutor(name);
        return warpBackgroundExecutor(executor);
    }

    public synchronized WorkTaskExecutor warpBackgroundExecutor(Executor executor) {
        if (executor != null) {
            this.backgroundExecutor = new SynchronousExecutor(executor);
        }
        return this;
    }

    public static Executor createDefaultExecutor(String name){
        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(2, Math.min(cpuCount - 1, 4));
        int maximumPoolSize = Math.max(20,cpuCount * 2 + 1);
        BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);
        if(name != null && name.length() > 0){
            name = name + " pool";
        }
        ThreadPoolExecutorConstructor threadPoolExecutor = new ThreadPoolExecutorConstructor(corePoolSize, maximumPoolSize,
                30L, TimeUnit.SECONDS, sPoolWorkQueue, new DefaultThreadFactory(name));
        threadPoolExecutor.setRejectedExecutionHandler(new WorkTaskRejectedExecutionHandler());
        threadPoolExecutor.setTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        return threadPoolExecutor;
    }

    private final Handler mMainThreadHandler = new LifecycleHandler(null, Looper.getMainLooper());

    private final Executor mMainThreadExecutor = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            postToMainThread(command);
        }
    };


    @Override
    public void postToMainThread(Runnable runnable) {
        postToMainThread(null,runnable);
    }

    public void postToMainThread(final LifecycleOwner owner, final Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            if (null != mMainThreadHandler) {
                mMainThreadHandler.post(runnable);
                if(owner != null) {
                    LifecycleThreadTaskHelper.create(new Runnable() {
                        @Override
                        public void run() {
                            mMainThreadHandler.removeCallbacks(runnable);
                            Log.i(TAG, "disposed by -->  " + owner + ",removeCallbacks runnable=" + runnable);
                        }
                    }, owner);
                }
            }
        }
    }

    @Override
    public void postToMainThreadDelayed(Runnable runnable, long delayMillis) {
        postToMainThreadDelayed(null,runnable,delayMillis);
    }


    public void postToMainThreadDelayed(final LifecycleOwner owner, @NonNull final Runnable runnable, long delayMillis) {
        if (null != mMainThreadHandler) {
            mMainThreadHandler.postDelayed(runnable, delayMillis);
            if(owner != null) {
                LifecycleThreadTaskHelper.create(new Runnable() {
                    @Override
                    public void run() {
                        mMainThreadHandler.removeCallbacks(runnable);
                        Log.i(TAG, "disposed by -->  " + owner + ",removeCallbacks runnable=" + runnable);
                    }
                }, owner);
            }
        }
    }

    @Override
    public void removeMainThreadCallbacks(Runnable runnable) {
        if (null != mMainThreadHandler) {
            //移除MainThreadHandler中对应的runnable
            mMainThreadHandler.removeCallbacks(runnable);
        }
    }

    @Override
    public Executor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }

    @Override
    public Future<?> executeOnBackgroundThread(Runnable runnable) {
        return executeOnBackgroundThread(null,runnable);
    }

    public Future<?> executeOnBackgroundThread(final LifecycleOwner owner, final Runnable runnable) {
        Future<?> future = null;
        if (null != backgroundExecutor) {
            try{
                future = backgroundExecutor.submit(runnable);
                if(owner != null) {
                    final Future<?> finalFuture = future;
                    LifecycleThreadTaskHelper.create(new Runnable() {
                        @Override
                        public void run() {
                            if(!finalFuture.isCancelled()) {
                                finalFuture.cancel(true);
                                Log.i(TAG, "disposed by -->  " + owner + ",cancel runnable=" + runnable);
                            }
                        }
                    }, owner);
                }
            }catch (Exception exception){
                Log.e(TAG,"executeOnBackgroundThread(" + runnable + "),exception " + exception);
            }
        }
        return future;
    }


    @Override
    public <T> Future<T> executeOnBackgroundThread(Callable<T> callable) {
        return executeOnBackgroundThread(null,callable);
    }


    public <T> Future<T> executeOnBackgroundThread(final LifecycleOwner owner, final Callable<T> callable) {
        Future<T> future = null;
        if (null != backgroundExecutor) {
            try{
                future = backgroundExecutor.submit(callable);
                if(owner != null) {
                    final Future<T> finalFuture = future;
                    LifecycleThreadTaskHelper.create(new Runnable() {
                        @Override
                        public void run() {
                            if(!finalFuture.isCancelled()) {
                                finalFuture.cancel(true);
                                Log.i(TAG, "disposed by -->  " + owner + ",cancel callable=" + callable);
                            }
                        }
                    }, owner);
                }
            }catch (Exception exception){
                Log.e(TAG,"executeOnBackgroundThread(" + callable + "),exception " + exception);
            }
        }
        return future;

    }


    @Override
    public TaskBackgroundExecutor getBackgroundExecutor() {
        return backgroundExecutor;
    }


    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
