package com.github.demo.ui.main;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.github.demo.api.Api;
import com.github.demo.databinding.ActivityMainBinding;
import com.github.demo.ui.recycler.RecyclerActivity;
import com.github.voisen.libmvp.base.BaseActivity;
import com.github.voisen.libmvp.network.INetworkProgressListener;
import com.github.voisen.libmvp.network.RetrofitCreator;
import com.github.voisen.libmvp.utils.RxUtils;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.HttpUrl;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainPresenter> implements MainContract.View {
    @Override
    protected void onViewLoaded() {
        mPresenter.login("admin", "admin");
    }

    @Override
    public void onLoginSuccess(Map<String, String> data) {
        showAlertMessage("登录成功");
    }

    @Override
    public void onLoginFail(Throwable e) {
        showAlertMessage(e.getLocalizedMessage());
    }

    private void testDownload() {
        String url = "https://dldir1.qq.com/weixin/android/weixin8050android2701_0x2800323e_arm64.apk";
        showProgress(-1, "请稍后");
        RetrofitCreator.create(Api.class, new INetworkProgressListener() {
                    @Override
                    public void onDownloadProgress(HttpUrl url, long total, long completed, long speedBytes) {
                        float v = completed * 1f / total;
                        mHandler.post(() -> {
                            showProgress(v, String.format(Locale.CHINA, "下载中\n%.2fMb/s", speedBytes / 1024.0f / 1024.0f));
                        });
                        Log.i(TAG, "onDownloadProgress: 进度: " + completed + "/" + total + ", speed: " + speedBytes);
                    }

                    @Override
                    public void onUploadProgress(HttpUrl url, long total, long completed, long speedBytes) {
                        Log.i(TAG, "onUploadProgress: " + completed + " | " + total);
                    }
                })
                .downloadFile(url)
                .compose(RxUtils.downloadFileHandler(new File(getExternalCacheDir() + "temp.data")))
                .subscribe(new AutoDisposableObserver<File>() {
                    @Override
                    public void onNext(@NonNull File file) {
                        mHandler.post(() -> {
                            showMessage("下载成功");
                            showLoading("处理中...");
                        });
                        mHandler.postDelayed(MainActivity.this::dismissLoading, 3000);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: 失败: ", e);
                        mHandler.post(() -> {
                            showMessage("下载失败: " + e.getLocalizedMessage());
                        });
                    }
                });
    }


    public void testRecyclerView(View view) {
        Intent intent = new Intent(this, RecyclerActivity.class);
        startActivity(intent);
    }

    public void testDownload(View view) {
        testDownload();
    }
}