package com.github.voisen.libmvp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.github.voisen.libmvp.widget.TinyToast;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public final class AppProfile {
    private static final String TAG = "MvpLib";
    public static void init(Context application){
        initRxjava();
        TinyToast.init(application);

        setSuccessIcon(application.getResources().getDrawable(R.drawable.libmvp_ic_successful));
        setInfoIcon(application.getResources().getDrawable(R.drawable.libmvp_ic_info));
        setErrorIcon(application.getResources().getDrawable(R.drawable.libmvp_ic_error));
    }

    private static void initRxjava() {
        RxJavaPlugins.setErrorHandler(throwable -> Log.e(TAG, "rxjava Unhandled exception: ", throwable));
    }

    private static Mode mMode = Mode.LIGHT;
    private static Mode mBarMode = Mode.LIGHT;

    private static Drawable mErrorDrawable;
    private static Drawable mSuccessDrawable;
    private static Drawable mInfoDrawable;

    public static void setMode(Mode mode) {
        mMode = mode;
    }

    public enum Mode{
        LIGHT,
        DARK
    }

    public enum Status{
        UNKNOWN,
        ERROR,
        INFO,
        SUCCESS
    }

    public static void setBarMode(Mode barMode) {
        AppProfile.mBarMode = barMode;
    }

    public static void setErrorIcon(Drawable drawable) {
        mErrorDrawable = drawable;
    }

    public static void setInfoIcon(Drawable drawable) {
        mInfoDrawable = drawable;
    }

    public static void setSuccessIcon(Drawable drawable) {
        mSuccessDrawable = drawable;
    }

    public static Mode getMode() {
        return mMode;
    }

    public static Drawable getErrorDrawable() {
        return mErrorDrawable;
    }

    public static Drawable getInfoDrawable() {
        return mInfoDrawable;
    }

    public static Drawable getSuccessDrawable() {
        return mSuccessDrawable;
    }

    public static Mode getBarMode() {
        return mBarMode;
    }
}
