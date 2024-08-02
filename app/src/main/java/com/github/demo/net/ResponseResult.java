package com.github.demo.net;

import com.github.voisen.libmvp.network.IResponseBody;

public class ResponseResult<T> implements IResponseBody<T> {

    private boolean status;
    private String message;
    private T data;
    private int code;

    @Override
    public boolean isSuccessful() {
        return status;
    }

    @Override
    public String errorMessage() {
        return message;
    }

    @Override
    public int errorCode() {
        return code;
    }

    @Override
    public T data() {
        return data;
    }
}
