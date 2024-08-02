package com.github.voisen.libmvp.exception;

import com.github.voisen.libmvp.network.IResponseBody;

public class ResponseErrorException extends Exception{

    private final int mErrorCode;

    public ResponseErrorException(int errorCode, String message) {
        super(message);
        mErrorCode = errorCode;
    }

    public ResponseErrorException createBy(IResponseBody responseBody){
        return new ResponseErrorException(responseBody.errorCode(), responseBody.errorMessage());
    }

}
