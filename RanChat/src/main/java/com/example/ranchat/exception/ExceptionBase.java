package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


public abstract class ExceptionBase extends RuntimeException{
    @Getter
    protected final ResponseCode errorCode;

    public ExceptionBase(ResponseCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ExceptionBase(ResponseCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    // 커스텀 예외는 Status 코드가 무조건 있어야 한다.
    public abstract HttpStatus getStatusCode();

}
