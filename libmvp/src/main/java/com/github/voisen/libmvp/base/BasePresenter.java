package com.github.voisen.libmvp.base;

import android.util.Log;

import com.github.voisen.libmvp.BuildConfig;
import com.github.voisen.libmvp.utils.Optional;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BasePresenter<M extends BaseModel, V extends BaseView> {
    protected final String TAG = getClass().getSimpleName();

    protected M mModel;
    protected V mView;

    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    protected abstract class AutoDisposableObserver<T> implements Observer<T> {
        private Disposable mDisposable = null;
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            mCompositeDisposable.add(d);
            mDisposable = d;
        }

        protected abstract void onFail(@NonNull Throwable e);

        @Override
        public final void onError(@NonNull Throwable e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "onError: ", e);
            }
            onFail(e);
            onFinished();
        }

        public void onFinished(){}
        @Override
        public final void onComplete() {
            onFinished();
            mCompositeDisposable.remove(mDisposable);
        }
    }

    protected abstract class HttpResponseObserver<T> extends AutoDisposableObserver<Optional<T>>{
        @Override
        public final void onNext(@NonNull Optional<T> tOptional) {
            this.onSuccess(tOptional.get());
        }
        protected abstract void onSuccess(@Nullable T data);
    }


    protected BasePresenter(){
        loadModel();
    }

    protected void attach(V view) {
        mView = view;
    }

    protected void detach() {
        mCompositeDisposable.dispose();
    }

    protected void loadModel(){
        try {
            Type genericSuperclass = Objects.requireNonNull(getClass().getSuperclass()).getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType){
                Type[] typeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                if (typeArguments.length > 1){
                    Class<?> modelClass = (Class<?>) typeArguments[0];
                    Constructor<?> constructor = modelClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    mModel = (M) constructor.newInstance();
                    Log.i(TAG, "loadModel: 加载模型成功: " + mModel);
                }
            }else{
                throw new IllegalStateException("模型加载失败...");
            }
        }catch (Exception e){
            Log.e(TAG, "getModel: Error: ", e);
        }
    }
}
