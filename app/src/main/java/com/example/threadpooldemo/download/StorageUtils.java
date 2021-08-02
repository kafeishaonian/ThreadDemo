package com.example.threadpooldemo.download;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.threadpooldemo.pool.WorkTaskExecutor;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class StorageUtils {

    private static final String tag = StorageUtils.class.getSimpleName();

    public static String SDCARD_PATH;
    public static String PENGPENG_DIR;
    public static String PENGPENG_RESOURCE;


    public StorageUtils(Context context){
        if (isAndroidQ()) {
            SDCARD_PATH = getDiskCacheDir(context);
        } else {
            SDCARD_PATH = Utils.getSdcardPathOld(context);
        }
        if (isAndroidQ()) {
            PENGPENG_DIR = SDCARD_PATH + File.separator;
        } else {
            PENGPENG_DIR = SDCARD_PATH + File.separator + Utils.getPackNameSimpleName(context) + File.separator;
        }

        PENGPENG_RESOURCE = PENGPENG_DIR + "resources" + File.separator;
    }


    public boolean isAndroidQ(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    private String getDiskCacheDir(Context context) {
        String cachePath = context.getFilesDir().getPath();
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && !Environment.isExternalStorageRemovable()) {
            try {
                cachePath = Utils.getSdcardPathOld(context) + "/Android/data/" + context.getPackageName() + "/files/"+Utils.getLastSimpleName(context);
                File file = new File(cachePath);
                Log.d(tag, "getDiskCacheDir.use.string.cachePath=$cachePath,dir.is.exists=${file.exists()}");
                if (!file.exists()) {
                    boolean isMake = file.mkdirs();
                    Log.d(tag, "getDiskCacheDir.use.string.cachePath=$cachePath,mkdir.result=$isMake}");
                    // 创建失败再用系统方式获取
                    if (!isMake) {
                        FutureTask futureTask = new FutureTask(new Callable() {
                            @Override
                            public Object call() throws Exception {
                                Log.d(tag, "getDiskCacheDir.use.system.cachePath=$cachePath");
                                return context.getExternalFilesDir(Utils.getLastSimpleName(context)).getPath();
                            }
                        });

                        WorkTaskExecutor.get().getBackgroundExecutor().submit(futureTask);
                        futureTask.get(500, TimeUnit.MILLISECONDS); //防止阻塞流程，规定500毫秒超时
                    }
                }
            } catch (Exception e) {
                Log.d(tag, "getExternalFilesDir.error.cachePath=$cachePath");
                Log.d(tag, e.getMessage());
            }
        }
        return cachePath;
    }

}
