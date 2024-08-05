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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.github.voisen.libmvp.AppProfile;
import com.github.voisen.libmvp.R;
import com.github.voisen.libmvp.widget.ProgressHUB;
import com.github.voisen.libmvp.widget.TinyDialog;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseFragment<VB extends ViewBinding, P extends BasePresenter> extends Fragment implements BaseView{


    protected final String TAG = getClass().getSimpleName();
    protected VB mViewBinding;
    protected P mPresenter;
    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    private ProgressHUB mProgressHUB;
    private DialogInterface mDialog = null;
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

    @Nullable
    @Override
    public View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initParams();
        return contentView();
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewLoaded();
    }

    @SuppressWarnings("unchecked")
    private void initParams() {
        mProgressHUB = new ProgressHUB(getContext());
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
                            mViewBinding = (VB) inflate.invoke(null, LayoutInflater.from(getContext()));
                        }
                    } else if (BasePresenter.class.isAssignableFrom(classType)) {
                        if (BasePresenter.class != classType){
                            mPresenter = (P) classType.newInstance();
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG, "initParams: 初始化失败: ", e);
                }
            }
        }
    }

    private View contentView() {
        if (mPresenter != null){
            mPresenter.attach(this);
        }
        if (mViewBinding == null){
            return null;
        }
        return mViewBinding.getRoot();
    }

    protected ProgressHUB getProgressHUB() {
        return mProgressHUB;
    }

    protected void dismissDialog(){
        if (mDialog == null){
            return;
        }
        mDialog.dismiss();
        mDialog = null;
    }

    protected void showDialog(Dialog dialog){
        dismissDialog();
        dialog.show();
        mDialog = dialog;
    }

    protected void showDialog(AlertDialog.Builder builder){
        dismissDialog();
        mDialog = builder.show();
    }


    @Override
    public void onDestroy() {
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
        mDialog = new TinyDialog.Builder(getContext())
                .setTitle(msg)
                .setCancelable(false)
                .setClickButtonDismiss(true)
                .setPositiveButton(getString(R.string.libmvp_ok), null)
                .show();
    }

    @Override
    public void showAlertMessage(CharSequence title, CharSequence msg, CharSequence cancelText, CharSequence sureText, DialogInterface.OnClickListener onOkClickListener) {
        dismissDialog();
        mDialog = new TinyDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setClickButtonDismiss(true)
                .setNegativeButton(cancelText, null)
                .setPositiveButton(sureText, onOkClickListener)
                .show();
    }

}
