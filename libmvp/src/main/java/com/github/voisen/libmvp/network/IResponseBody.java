package com.github.voisen.libmvp.network;

public interface IResponseBody<T> {

    /**
     * 标记该请求是否是成功的
     * @return r
     */
    boolean isSuccessful();

    /**
     * 错误信息
     * @return r
     */
    String errorMessage();

    /**
     * 错误码
     * @return r
     */
    int errorCode();

    /**
     * 请求数据
     * @return r
     */
    T data();
}
