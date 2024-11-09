package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


public abstract class ExceptionBase extends RuntimeException{
    @Getter
    protected final ResponseCode errorCode;

    public ExceptionBase(ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // abstract 메서드 -> ResponseCode 에서 꺼내기
    public HttpStatus getStatusCode() {
        return errorCode.getHttpStatus();
    }

}
