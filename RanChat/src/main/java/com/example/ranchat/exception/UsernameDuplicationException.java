package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class UsernameDuplicationException extends ExceptionBase{
    public UsernameDuplicationException(ResponseCode responseCode) {
        super(responseCode);
    }
}
