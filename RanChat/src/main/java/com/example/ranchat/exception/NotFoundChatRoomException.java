package com.example.ranchat.exception;

import com.example.ranchat.response.ResponseCode;

public class NotFoundChatRoomException extends ExceptionBase {
	public NotFoundChatRoomException(ResponseCode responseCode) {
		super(responseCode);
	}

}
