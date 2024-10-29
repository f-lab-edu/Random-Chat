package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundUserException extends ExceptionBase{
    public NotFoundUserException(ResponseCode responseCode) {
        errorCode = responseCode;
    }

    public NotFoundUserException(ResponseCode responseCode, @Nullable String message) {
        errorCode = responseCode;
        errorMessage = message;
    }
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.NOT_FOUND;
    }


}
