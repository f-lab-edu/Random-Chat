package com.example.ranchat.exception;

import com.example.ranchat.response.ErrorHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ExceptionResolver {
    // ExceptionBase를 상속받는 모든 커스텀 예외를 처리
    @ExceptionHandler(ExceptionBase.class)
    public ResponseEntity<ErrorHandlerResponse> userNotFoundExceptionHandler(ExceptionBase exception) {
        log.error("Custom Exception: {}", exception.getMessage(), exception);
        ErrorHandlerResponse errorResponse = new ErrorHandlerResponse(exception);
        HttpStatus httpStatus = errorResponse.getStatus();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorHandlerResponse> validationExceptionHandler(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        List<String> errorMessagesForLog = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        List<String> errorMessagesForClient = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": 유효하지 않은 값입니다.")
                .collect(Collectors.toList());
        log.error("Validation Exception: {}", errorMessagesForLog, exception);
        // 문제 검증 메세지가 하나가 아니야
        return new ResponseEntity<>(new ErrorHandlerResponse(exception,errorMessagesForClient),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHandlerResponse> handleGeneralException(HttpServletRequest request, Exception exception) {
        log.error("Unhandled Exception: {}", exception.getMessage(), exception);
        ErrorHandlerResponse errorResponse = new ErrorHandlerResponse(exception);
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }

}
