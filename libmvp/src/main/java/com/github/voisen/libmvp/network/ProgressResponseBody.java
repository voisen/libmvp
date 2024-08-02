package com.github.voisen.libmvp.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.voisen.libmvp.utils.SpeedCounter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final HttpUrl url;
    private final INetworkProgressListener progressListener;
    private final long progressInterval;

    private BufferedSource mSource = null;
    private final String TAG = "ProgressResponseBody";

    public ProgressResponseBody(HttpUrl url, ResponseBody responseBody, INetworkProgressListener progressListener, long progressInterval) {
        this.url = url;
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.progressInterval = progressInterval;
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public void close() {
        super.close();
        responseBody.close();
        if (mSource != null && mSource.isOpen()){
            try {
                mSource.close();
            } catch (IOException e) {
            }
        }
    }

    @NonNull
    @Override
    public BufferedSource source() {
        if (mSource != null){
            return mSource;
        }
        mSource = Okio.buffer(new ForwardingSource(responseBody.source()) {
            private final AtomicLong completedBytes = new AtomicLong(0);
            private final SpeedCounter mSpeedCounter = new SpeedCounter();
            private long lastUpdateTime = 0;
            private long lastCompletedBytes = -1;
            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long read;
                long contentedLength = contentLength();
                try {
                    read = super.read(sink, byteCount);
                    long l = completedBytes.addAndGet(Math.max(read, 0));
                    if (progressListener != null){
                        mSpeedCounter.updateValue(completedBytes.get());
                        long millis = System.currentTimeMillis();
                        if ((millis - lastUpdateTime > progressInterval || l == contentedLength) && lastCompletedBytes != l){
                            lastUpdateTime = millis;
                            lastCompletedBytes = l;
                            progressListener.onDownloadProgress(url, contentedLength, l, mSpeedCounter.getSpeedPerSeconds());
                        }
                    }
                }catch (Exception e){
                    if (progressListener != null){
                        progressListener.onDownloadProgress(url, contentedLength, completedBytes.get(), 0);
                    }
                    throw e;
                }
                return read;
            }
        });
        return mSource;
    }
}
