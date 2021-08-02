package com.example.threadpooldemo.download;

public interface CacheDownloadListener {

    void onSuccess();

    void onProgress(float percent, long currentSize, long totalSize);

    void onFailure(Throwable throwable);

}
