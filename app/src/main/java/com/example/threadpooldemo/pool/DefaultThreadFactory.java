package com.example.threadpooldemo.pool;

import android.util.Log;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工程
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static final String TAG = DefaultThreadFactory.class.getSimpleName();

    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup threadGroup;
    private final String namePrefix;

    public DefaultThreadFactory() {
        this(null, "Thread pool");
    }

    public DefaultThreadFactory(String namePrefix) {
        this(null,namePrefix);
    }

    public DefaultThreadFactory(ThreadGroup threadGroup, String namePrefix) {
        if(null == threadGroup){
            SecurityManager s = System.getSecurityManager();
            this.threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }else {
            this.threadGroup = threadGroup;
        }
        if(namePrefix == null || namePrefix.length() == 0){
            namePrefix = "Thread pool";
        }
        this.namePrefix =  namePrefix + "_" + poolNumber.getAndIncrement() + "-thread";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = namePrefix + "_" +  threadNumber.getAndIncrement();
        Log.i(TAG, "Thread production, name is [" + threadName + "]");
        Thread thread = new Thread(threadGroup, runnable, threadName, 0);
        if (thread.isDaemon()) {   //设为非后台线程
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) { //优先级为normal
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        // 捕获多线程处理中的异常
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(TAG, "Running task appeared exception! Thread [" + thread.getName() + "], because [" + ex.getMessage() + "]");
            }
        });
        return thread;
    }
}
