package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;

public class NotFoundUserException extends ExceptionBase {
	public NotFoundUserException(ResponseCode responseCode) {
		super(responseCode);
	}

}
