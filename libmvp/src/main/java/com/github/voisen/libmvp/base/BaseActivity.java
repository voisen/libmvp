package com.github.voisen.libmvp.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.github.voisen.libmvp.AppProfile;
import com.github.voisen.libmvp.R;
import com.github.voisen.libmvp.widget.ProgressHUB;
import com.github.voisen.libmvp.widget.TinyDialog;
import com.github.voisen.libmvp.widget.TinyToast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseActivity<VB extends ViewBinding, P extends BasePresenter> extends AppCompatActivity implements BaseView {

    protected final String TAG = getClass().getSimpleName();
    protected VB mViewBinding;
    protected P mPresenter;
    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    private ProgressHUB mProgressHUB;
    private DialogInterface mDialog = null;
    private static int mDefaultScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    protected abstract class AutoDisposableObserver<T> implements Observer<T> {
        private Disposable mDisposable = null;
        @CallSuper
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            mCompositeDisposable.add(d);
            mDisposable = d;
        }
        public void onFinished(){}
        @Override
        public final void onComplete() {
            onFinished();
            mCompositeDisposable.remove(mDisposable);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
        beforeInitContentView();
        initContentView();
        afterInitContentView();
        onViewLoaded();
    }

    protected void afterInitContentView() {
        getWindow().getDecorView().setOnTouchListener(this::onDecorViewTouch);
    }

    protected boolean onDecorViewTouch(View view, MotionEvent event) {
        KeyboardUtils.hideSoftInput(this);
        return true;
    }

    @SuppressWarnings("unchecked")
    private void initParams() {
        mProgressHUB = new ProgressHUB(this);
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            for (Type argument : typeArguments) {
                try {
                    Class<?> classType = (Class<?>) argument;
                    if (ViewBinding.class.isAssignableFrom(classType)) {
                        if (ViewBinding.class != classType){
                            Method inflate = classType.getMethod("inflate", LayoutInflater.class);
                            mViewBinding = (VB) inflate.invoke(null, LayoutInflater.from(this));
                        }
                    } else if (BasePresenter.class.isAssignableFrom(classType)) {
                        if (BasePresenter.class != classType){
                            Constructor<?> constructor = classType.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            mPresenter = (P) constructor.newInstance();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, "initParams: 初始化失败: ", e);
                }
            }
        }
        int screenOrientation = getScreenOrientation();
        if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED){
            setRequestedOrientation(screenOrientation);
        }
    }

    private void initContentView() {
        //获取第一个参数
        if (mViewBinding != null){
            setContentView(mViewBinding.getRoot());
        }
        if (mPresenter != null){
            mPresenter.attach(this);
        }
    }

    public static void setDefaultScreenOrientation(int defaultScreenOrientation){
        mDefaultScreenOrientation = defaultScreenOrientation;
    }

    protected ProgressHUB getProgressHUB() {
        return mProgressHUB;
    }

    protected boolean statusBarIsLightMode(){
        return AppProfile.getBarMode() == AppProfile.Mode.LIGHT;
    }

    protected void dismissDialog(){
        if (mDialog == null){
            return;
        }
        mDialog.dismiss();
        mDialog = null;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        if (mPresenter != null){
            mPresenter.detach();
        }
        mCompositeDisposable.dispose();
        dismissDialog();
        dismissLoading();
        super.onDestroy();
    }

    /**
     * 视图加载完成后调用
     */
    protected abstract void onViewLoaded();

    protected void beforeInitContentView(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        BarUtils.transparentStatusBar(this);
        BarUtils.setStatusBarLightMode(getWindow(), statusBarIsLightMode());
    }

    protected int getScreenOrientation(){
        return mDefaultScreenOrientation;
    }

    @Override
    public void showLoading(CharSequence msg) {
        mProgressHUB.showLoading(msg);
    }

    @Override
    public void dismissLoading() {
        mProgressHUB.dismiss();
    }

    @Override
    public void showMessage(CharSequence msg) {
        showMessage(null, msg);
    }

    @Override
    public void showMessage(Drawable drawable, CharSequence msg) {
        mProgressHUB.showMessage(drawable, msg, Math.min(Math.max(1500, msg.length() * 100), 3000));
    }

    @Override
    public void showMessageWithStatus(AppProfile.Status status, CharSequence msg) {
        mProgressHUB.showMessageWithStatus(status, msg, Math.min(Math.max(1500, msg.length() * 100), 3000));
    }

    @Override
    public void showProgress(float progress, CharSequence msg) {
        mProgressHUB.showProgress(progress, msg);
    }

    @Override
    public void showAlertMessage(CharSequence msg) {
        dismissDialog();
        mDialog = new TinyDialog.Builder(this)
                .setTitle(msg)
                .setCancelable(false)
                .setClickButtonDismiss(true)
                .setPositiveButton(getString(R.string.libmvp_ok), null)
                .show();
    }

    @Override
    public void showAlertMessage(CharSequence title, CharSequence msg, CharSequence cancelText, CharSequence sureText, DialogInterface.OnClickListener onOkClickListener) {
        dismissDialog();
        mDialog = new TinyDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setClickButtonDismiss(true)
                .setNegativeButton(cancelText, null)
                .setPositiveButton(sureText, onOkClickListener)
                .show();
    }
}
