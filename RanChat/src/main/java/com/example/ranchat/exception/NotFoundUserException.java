package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class NotFoundUserException extends ExceptionBase{
    public NotFoundUserException(ResponseCode responseCode) {
        super(responseCode);
    }

}
