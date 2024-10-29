package com.example.ranchat.response;

import com.example.ranchat.exception.ExceptionBase;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class ErrorHandlerResponse {
    private HttpStatus status;
    // 단일 메세지와 다중 메세지를 다 처리 가능
    private List<String> message = new ArrayList<>();
    private ResponseCode errorCode;
    private LocalDateTime timestamp;

    // 커스텀 예외를 처리하는 생성자
    public ErrorHandlerResponse(ExceptionBase exception) {
        this.status = exception.getStatusCode();
        // null 처리를 다 해줘야 하나?? 어느 경우에 null 처리를 하고 어느 경우에 null 처리를 안해도 되는가..
        this.message.add(exception.getMessage());
        this.errorCode = exception.getErrorCode();
        this.timestamp = LocalDateTime.now();
    }

    // 일반 생성자를 처리하는 생성자
    public ErrorHandlerResponse(Exception exception)
    {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message.add(exception.getMessage());
        this.errorCode = ResponseCode.UN_KNOWN_ERROR;
        this.timestamp = LocalDateTime.now();
    }
    // 유효성 검증 실패 Response
    public ErrorHandlerResponse(Exception exception,List<String> errors){
        this.status = HttpStatus.BAD_REQUEST;
        // null 처리
        this.message = Optional.ofNullable(errors).orElse(new ArrayList<>());
        this.errorCode = ResponseCode.INVALID_PARAMETER;
        this.timestamp = LocalDateTime.now();
    }


}
