package com.example.threadpooldemo.pool;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.atomic.AtomicBoolean;

public class LifecycleThreadTaskHelper implements LifecycleProvider.Observer{

    private final Runnable runnable;
    private final Lifecycle.Event event;
    private final LifecycleProvider provider;

    private final AtomicBoolean once = new AtomicBoolean();

    public static LifecycleThreadTaskHelper create(Runnable runnable, LifecycleOwner owner) {
        return new LifecycleThreadTaskHelper(runnable,Lifecycle.Event.ON_DESTROY, AndroidLifecycle.createLifecycleProvider(owner));
    }

    public LifecycleThreadTaskHelper(Runnable runnable, Lifecycle.Event event, LifecycleProvider provider) {
        this.runnable = runnable;
        this.event = event;
        this.provider = provider;
        if(provider != null) {
            provider.observe(this);
        }
    }


    @Override
    public void onChanged(@NonNull Lifecycle.Event event) {
        if (this.event == event
                || event == Lifecycle.Event.ON_DESTROY
                //Activity和Fragment的生命周期是不会传入 {@code Lifecycle.Event.ON_ANY},
                //可以手动调用此方法传入 {@code Lifecycle.Event.ON_ANY},用于区分是否为手动调用
                || event == Lifecycle.Event.ON_ANY) {
            /*保证原子性*/
            if (once.compareAndSet(false, true)) {
                if(runnable != null) {
                    runnable.run();
                }
            }
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            provider.removeObserver(this);
        }
    }
}
