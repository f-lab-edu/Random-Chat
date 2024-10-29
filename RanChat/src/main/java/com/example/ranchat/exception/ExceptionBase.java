package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;


public abstract class ExceptionBase extends RuntimeException{
    // 커스텀 예외는 Status 코드가 무조건 있어야 한다.
    public abstract HttpStatus getStatusCode();

    protected String errorMessage;
    //
    @Getter
    protected ResponseCode errorCode;

    @Override
    public String getMessage() {
        return errorMessage;
    }


}
