package com.example.threadpooldemo.download;

import io.reactivex.disposables.Disposable;

public class Task {

    private String url;
    private String path = StorageUtils.PENGPENG_RESOURCE;
    private String md5;
    private int priority;
    private boolean highPriority;
    private CacheDownloadListener listener;
    private volatile Disposable disposable;
    private String from;

    public Task setFrom(String from) {
        this.from = from;
        return this;
    }

    public Task setUrl(String url) {
        this.url = url;
        return this;
    }

    public Task setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
        return this;
    }

    public Task setPath(String path) {
        this.path =  StorageUtils.PENGPENG_RESOURCE;
        return this;
    }

    public Task setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public Task setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public Task setDisposable(Disposable disposable){
        this.disposable = disposable;
        return this;
    }

    public Task setListener(CacheDownloadListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public String toString() {
        return "Task{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", md5='" + md5 + '\'' +
                ", priority=" + priority +
                ", highPriority=" + highPriority +
                ", listener=" + listener +
                ", disposable=" + disposable +
                ", from='" + from + '\'' +
                '}';
    }
}
