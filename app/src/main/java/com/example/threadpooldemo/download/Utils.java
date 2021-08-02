package com.example.threadpooldemo.download;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

public class Utils {

    public static String getSdcardPathOld(Context context) {
        return externalMemoryAvailable() && Environment.getExternalStorageDirectory() != null ?
                Environment
                        .getExternalStorageDirectory().getAbsolutePath() : context.getCacheDir()
                .getAbsolutePath();
    }

    public static boolean externalMemoryAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取包名的最后一位
     */
    public static String getLastSimpleName(Context context) {
        try {
            // 主播后缀:.uplive
            //5.3.5 pro包使用主包后缀
            String e = context.getPackageName();
            if (!TextUtils.isEmpty(e)) {
                if (e.endsWith(".pro") || e.endsWith(".uparty")) {
                    return "uplive";
                }
                return e.substring(e.lastIndexOf(".") + 1);
            }
        } catch (Exception var1) {
            var1.printStackTrace();
        }
        return "uplive";
    }

    public static String getPackNameSimpleName(Context context) {
        try {
            // 主播后缀:.uplive
            //5.3.5 pro包使用主包后缀
            String e = context.getPackageName();
            if (!TextUtils.isEmpty(e)) {
                if (e.endsWith(".pro") || e.endsWith(".uparty")) {
                    return ".uplive";
                }
                return e.substring(e.lastIndexOf("."));
            }
        } catch (Exception var1) {
            var1.printStackTrace();
        }
        return ".pengpeng";
    }
}
