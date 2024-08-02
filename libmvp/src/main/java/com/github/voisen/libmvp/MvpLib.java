package com.github.voisen.libmvp;

import android.content.Context;
import android.util.Log;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public final class MvpLib {
    private static final String TAG = "MvpLib";
    public static void init(Context application){
        initRxjava();
    }

    private static void initRxjava() {
        RxJavaPlugins.setErrorHandler(throwable -> Log.e(TAG, "rxjava Unhandled exception: ", throwable));
    }
}
