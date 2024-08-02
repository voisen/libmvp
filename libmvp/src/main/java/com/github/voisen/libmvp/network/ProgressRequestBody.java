package com.github.voisen.libmvp.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.voisen.libmvp.utils.SpeedCounter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    private final RequestBody requestBody;
    private final INetworkProgressListener progressListener;
    private final HttpUrl url;
    private final long progressInterval;

    public ProgressRequestBody(HttpUrl url, RequestBody requestBody, INetworkProgressListener progressListener, long progressInterval) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
        this.url= url;
        this.progressInterval = progressInterval;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {
        BufferedSink buffer = Okio.buffer(getSink(bufferedSink));
        requestBody.writeTo(buffer);
        buffer.flush();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    private Sink getSink(BufferedSink bufferedSink){
        return new ForwardingSink(bufferedSink) {
            private final AtomicLong completedBytes = new AtomicLong(0);
            private final SpeedCounter mSpeedCounter = new SpeedCounter();
            private long lastUpdateTime = 0;
            private long lastCompletedBytes = 0;
            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                long l = completedBytes.addAndGet(Math.max(0, byteCount));
                if (progressListener != null){
                    mSpeedCounter.updateValue(l);
                    long millis = System.currentTimeMillis();
                    if ((millis - lastUpdateTime > progressInterval || l == contentLength()) && lastCompletedBytes != l){
                        lastUpdateTime = millis;
                        lastCompletedBytes = l;
                        progressListener.onUploadProgress(url, contentLength(), l, mSpeedCounter.getSpeedPerSeconds());
                    }
                }
            }
        };
    }
}
