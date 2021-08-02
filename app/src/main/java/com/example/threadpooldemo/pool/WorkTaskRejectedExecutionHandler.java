package com.example.threadpooldemo.pool;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkTaskRejectedExecutionHandler implements RejectedExecutionHandler {

    private static final int BACKUP_POOL_SIZE = 5;
    private static final int KEEP_ALIVE_SECONDS = 3;
    public static final String REJECTED_THREAD_POOL_NAME = "rejected_thread_pool_name";
    private static volatile ThreadPoolExecutorConstructor sBackupExecutor;


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        Log.d("WorkTaskExecutor","WorkTaskRejectedExecutionHandler rejectedExecution  Runnable " + r);
        if (null == sBackupExecutor) {
            synchronized (this) {
                if (sBackupExecutor == null) {
                    LinkedBlockingQueue<Runnable> sBackupExecutorQueue = new LinkedBlockingQueue<Runnable>();
                    DefaultThreadFactory threadFactory = new DefaultThreadFactory("Rejected Thread pool");
                    sBackupExecutor = new ThreadPoolExecutorConstructor(
                            BACKUP_POOL_SIZE, BACKUP_POOL_SIZE, KEEP_ALIVE_SECONDS,
                            TimeUnit.SECONDS, sBackupExecutorQueue, threadFactory,new ThreadPoolExecutor.DiscardOldestPolicy());
                    sBackupExecutor.setTimeout(60 * 1000, TimeUnit.MILLISECONDS);
                    sBackupExecutor.allowCoreThreadTimeOut(true);
                    WorkTaskExecutor.get(REJECTED_THREAD_POOL_NAME).warpBackgroundExecutor(sBackupExecutor);
                }
            }
        }
        WorkTaskExecutor.get(REJECTED_THREAD_POOL_NAME).executeOnBackgroundThread(r);
    }
}
