package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class UsernameDuplicationException extends ExceptionBase{
    public UsernameDuplicationException(ResponseCode responseCode) {
        errorCode = responseCode;
    }

    public UsernameDuplicationException(ResponseCode responseCode, @Nullable String message) {
        errorCode = responseCode;
        errorMessage = message;
    }
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.CONFLICT;
    }
}
