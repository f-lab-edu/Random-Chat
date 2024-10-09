package com.example.ranchat.exception;

import com.example.ranchat.response.ErrorHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class exceptionResolver {
    @ExceptionHandler({NotFoundUserException.class})
    @ResponseBody
    public ResponseEntity<ErrorHandlerResponse> userNotFoundExceptionHandler(HttpServletRequest request, Exception exception) {
        ErrorHandlerResponse errorResponse = new ErrorHandlerResponse(exception);
        HttpStatus httpStatus = errorResponse.getStatus();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorHandlerResponse> validationExceptionHandler(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        List<String> errorMessages = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        // 문제 검증 메세지가 하나가 아니야
        return new ResponseEntity<>(new ErrorHandlerResponse(exception,errorMessages),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameDuplicationException.class)
    @ResponseBody
    public ResponseEntity<ErrorHandlerResponse> UsernameDuplicationExceptionHandler(UsernameDuplicationException exception) {
        ErrorHandlerResponse errorResponse = new ErrorHandlerResponse(exception);
        HttpStatus httpStatus = errorResponse.getStatus();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

}
