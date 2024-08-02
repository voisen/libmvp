package com.github.voisen.libmvp.network;

import androidx.annotation.NonNull;

import com.github.voisen.libmvp.BuildConfig;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCreator {

    private static final LoggingInterceptor.Builder mLoggingInterceptor = new LoggingInterceptor.Builder()
            .setLevel(BuildConfig.DEBUG?Level.BASIC:Level.NONE)
            .maxLogBytes(102400);

    private static final Retrofit.Builder mRetrofitBuilder = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    private static final OkHttpClient.Builder mOkhttpBuilder = new OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .callTimeout(0, TimeUnit.SECONDS)
            .connectTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS);

    private static String BASE_URL = null;

    public static OkHttpClient.Builder getOkhttpBuilder() {
        return mOkhttpBuilder;
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    public static void init(Level logLevel, String baseUrl){
        mLoggingInterceptor.setLevel(logLevel);
        mOkhttpBuilder.addInterceptor(mLoggingInterceptor.build());
        BASE_URL = baseUrl;
        if (!BASE_URL.endsWith("/")) {
            BASE_URL += "/";
        }
    }

    private static Retrofit createRetrofit(OkHttpClient client) {
        return mRetrofitBuilder
                .baseUrl(BASE_URL)
                .client(client)
                .build();
    }

    public static<T> T create(Class<T> apiService){
        if (BASE_URL == null){
            throw new IllegalArgumentException("retrofit has not been initialized, call the init method !");
        }
        return createRetrofit(mOkhttpBuilder.build())
                .create(apiService);
    }

    public static<T> T create(Class<T> apiService, INetworkProgressListener progressListener){
        return create(apiService, progressListener, 15);
    }

    public static<T> T create(Class<T> apiService, INetworkProgressListener progressListener, long progressInterval){
        OkHttpClient.Builder builder = mOkhttpBuilder.build()
                .newBuilder()
                .addNetworkInterceptor(new ProgressInterceptor(progressListener, progressInterval));
        return createRetrofit(builder.build())
                .create(apiService);
    }

    protected static class ProgressInterceptor implements Interceptor{
        private final INetworkProgressListener mProgressListener;
        private final long mProgressInterval;
        public ProgressInterceptor(INetworkProgressListener listener, long progressInterval) {
            mProgressListener = listener;
            mProgressInterval = progressInterval;
        }

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request chainRequest = chain.request();
            HttpUrl url = chainRequest.url();
            Request request = chainRequest.newBuilder()
                    .method(chainRequest.method(), chainRequest.body()==null?null:new ProgressRequestBody(url, chainRequest.body(), mProgressListener, mProgressInterval))
                    .build();
            Response response = chain.proceed(request);
            Response.Builder responseBuilder = response.newBuilder()
                    .body(new ProgressResponseBody(url, response.body(), mProgressListener, mProgressInterval));
            return responseBuilder.build();
        }
    }
}
