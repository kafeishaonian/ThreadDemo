package com.example.threadpooldemo.pool;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

/**
 * 统一分发Activity和 Fragment的生命周期时间.
 */
public interface LifecycleProvider {

    void observe(Observer observer);

    void removeObserver(Observer observer);

    interface Observer {
        /**
         * 当Activity或Fragment生命周期发生变化时回调
         */
        void onChanged(@NonNull Lifecycle.Event event);
    }

}
