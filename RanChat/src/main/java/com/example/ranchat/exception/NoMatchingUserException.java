package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;

public class NoMatchingUserException extends ExceptionBase{
    public NoMatchingUserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
