package com.github.demo.ui.main;

import android.util.Log;

import com.github.demo.api.Api;
import com.github.voisen.libmvp.network.INetworkProgressListener;
import com.github.voisen.libmvp.network.RetrofitCreator;
import com.github.voisen.libmvp.utils.Optional;
import com.github.voisen.libmvp.utils.RxUtils;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.HttpUrl;

public class MainModel implements MainContract.Model {
    private final String TAG = this.getClass().getSimpleName();
    @Override
    public Observable<Optional<Map<String, String>>> login(String username, String password) {
        return RetrofitCreator.create(Api.class, new INetworkProgressListener() {
                    @Override
                    public void onUploadProgress(HttpUrl url, long total, long completed, long speedBytes) {
                        Log.i(TAG, "onUploadProgress: 进度: "+ completed + "/" + total);
                    }

                    @Override
                    public void onDownloadProgress(HttpUrl url, long total, long completed, long speedBytes) {
                        Log.i(TAG, "onDownloadProgress: 下载进度: "+ completed + "/" + total);
                    }
                })
                .login(username, password)
                .compose(RxUtils.handlerResponseWithThreadScheduling());
    }
}
