package com.github.voisen.libmvp.network;

import okhttp3.HttpUrl;

public interface INetworkProgressListener {
    default void onUploadProgress(HttpUrl url, long total, long completed, long speedBytes){}
    default void onDownloadProgress(HttpUrl url, long total, long completed, long speedBytes){}
}
