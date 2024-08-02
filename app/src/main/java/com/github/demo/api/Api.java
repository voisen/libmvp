package com.github.demo.api;
import com.github.demo.net.ResponseResult;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface Api {

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);


    @POST("test/login")
    @FormUrlEncoded
    Observable<ResponseResult<Map<String, String>>> login(@Field("username") String username, @Field("password") String password);

}
