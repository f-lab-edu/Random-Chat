package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

public class JwtInvalidException extends ExceptionBase{
    public JwtInvalidException(ResponseCode responseCode) {
        super(responseCode);
    }

    public JwtInvalidException(ResponseCode responseCode, @Nullable String message) {
        super(responseCode, message);
    }
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}
