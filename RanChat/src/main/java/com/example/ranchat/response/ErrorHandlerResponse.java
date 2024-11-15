package com.example.ranchat.response;

import com.example.ranchat.exception.ExceptionBase;
import lombok.Builder;
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
    private ResponseCode responseCode;
    private LocalDateTime timestamp;

    @Builder
    public ErrorHandlerResponse(HttpStatus httpStatus, List<String> message, ResponseCode responseCode, LocalDateTime timestamp) {
        this.status = httpStatus;
        // null 처리를 다 해줘야 하나?? 어느 경우에 null 처리를 하고 어느 경우에 null 처리를 안해도 되는가..
        this.message = message;
        this.responseCode = responseCode;
        this.timestamp = timestamp;
    }

    // 커스텀 예외를 처리하는 팩토리 메서드
    public static ErrorHandlerResponse fromException(ExceptionBase exceptionBase) {
        return ErrorHandlerResponse.builder()
                .responseCode(exceptionBase.getResponseCode())
                .httpStatus(exceptionBase.getResponseCode().getHttpStatus())
                .message(List.of(exceptionBase.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 일반 예외를 처리하는 생성자
    public static ErrorHandlerResponse fromException(Exception exception) {
        ResponseCode responseCode = ResponseCode.UN_KNOWN_ERROR;
        return ErrorHandlerResponse.builder()
                .responseCode(responseCode)
                .httpStatus(responseCode.getHttpStatus())
                .message(List.of(responseCode.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorHandlerResponse fromValidationException(List<String> errors) {
        return ErrorHandlerResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .responseCode(ResponseCode.UN_KNOWN_ERROR)
                .message(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
