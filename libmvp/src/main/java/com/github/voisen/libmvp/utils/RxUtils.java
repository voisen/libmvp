package com.github.voisen.libmvp.utils;

import android.util.Log;

import com.github.voisen.libmvp.exception.ResponseErrorException;
import com.github.voisen.libmvp.network.IResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public final class RxUtils {
    private static final String TAG = "RxUtils";

    public static<T> ObservableTransformer<T, T> threadScheduling() {
        return new ObservableTransformer<T, T>() {
            @Override
            public @NonNull ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 处理请求结果信息
     * @return r
     * @param <T> t
     */
    public static <T> ObservableTransformer<IResponseBody<T>, Optional<T>> handlerResponse(){
        return new ObservableTransformer<IResponseBody<T>, Optional<T>>() {
            @Override
            public @NonNull ObservableSource<Optional<T>> apply(@NonNull Observable<IResponseBody<T>> upstream) {
                return upstream.map(tiResponseBody -> {
                    if (tiResponseBody.isSuccessful()){
                        return Optional.of(tiResponseBody.data());
                    }
                    throw new ResponseErrorException(tiResponseBody.errorCode(), tiResponseBody.errorMessage());
                });
            }
        };
    }

    /**
     * 处理数据和线程调度一并执行
     * @return r
     * @param <T> t
     */
    public static <T> ObservableTransformer<IResponseBody<T>, Optional<T>> handlerResponseWithThreadScheduling(){
        return new ObservableTransformer<IResponseBody<T>, Optional<T>>() {
            @Override
            public @NonNull ObservableSource<Optional<T>> apply(@NonNull Observable<IResponseBody<T>> upstream) {
                return upstream.map(tiResponseBody -> {
                    if (tiResponseBody.isSuccessful()){
                        return Optional.of(tiResponseBody.data());
                    }
                    throw new ResponseErrorException(tiResponseBody.errorCode(), tiResponseBody.errorMessage());
                }).compose(RxUtils.threadScheduling());
            }
        };
    }



    /**
     * 下载文件的rxjava转换工具，这个自带线程调度功能
     * @param toDestFile 目标文件地址
     * @return r
     */
    public static ObservableTransformer<ResponseBody, File> downloadFileHandler(@NonNull File toDestFile){
        return new ObservableTransformer<ResponseBody, File>(){
            @Override
            public @NonNull ObservableSource<File> apply(@NonNull Observable<ResponseBody> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map(responseBody -> {
                    if (toDestFile == null){
                        throw new IllegalArgumentException("目标文件不能为空。");
                    }
                    Log.w(TAG, "apply: " + Thread.currentThread());
                    File parentFile = toDestFile.getParentFile();
                    if (parentFile.exists()){
                        parentFile.mkdirs();
                    }
                    try(FileOutputStream fileOutputStream = new FileOutputStream(toDestFile)){
                        InputStream inputStream = responseBody.byteStream();
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = inputStream.read(buffer)) != -1){
                            fileOutputStream.write(buffer, 0, len);
                        }
                        inputStream.close();
                        fileOutputStream.flush();
                    }catch (Exception e){
                        if (toDestFile.exists()){
                            toDestFile.delete();
                        }
                        throw e;
                    }
                    return toDestFile;
                }).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
