package com.example.ranchat.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
@Getter
public enum ResponseCode {
    // HTTP_CODE 200
    SUCCESS(2000, "OK",HttpStatus.OK),

    // HTTP_CODE 204
    ACCEPTED(2041, "Accepted",HttpStatus.NO_CONTENT),

    // HTTP_CODE 400
    MISSING_REQUIRED_PARAMETER(4001, "Missing required parameter",HttpStatus.BAD_REQUEST),
    INVALID_PARAMETER(4002, "Invalid parameter",HttpStatus.BAD_REQUEST),

    // HTTP_CODE 401
    NO_AUTH_TOKEN(4011, "No auth token provided", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_TOKEN(4012, "Invalid auth token", HttpStatus.UNAUTHORIZED),
    INVALID_AUTH_FORMAT(4013, "Invalid auth token format", HttpStatus.UNAUTHORIZED),
    EXPIRED_AUTH_TOKEN(4014, "Expired auth token", HttpStatus.UNAUTHORIZED),
    FAILED_LOGIN(4015, "Failed login attempt", HttpStatus.UNAUTHORIZED),
    DUPLICATED_LOGIN(4016, "Duplicated login", HttpStatus.UNAUTHORIZED),
    INVALID_VERIFICATION_CODE(4017, "Invalid verification code", HttpStatus.UNAUTHORIZED),
    INVALID_DEVICE_TOKEN(4018, "Invalid device token", HttpStatus.UNAUTHORIZED),

    // HTTP_CODE 403
    NOT_ALLOWED(4031, "Not allowed", HttpStatus.FORBIDDEN),
    NOT_ADMIN_USER(4032, "Not an admin user", HttpStatus.FORBIDDEN),
    NOT_FOUND_USER(4033, "User not found", HttpStatus.FORBIDDEN),

    // HTTP_CODE 409
    DUPLICATED_USERNAME(4016, "이미 존재하는 username 입니다.", HttpStatus.CONFLICT),

    // HTTP_CODE 500
    UN_KNOWN_ERROR(5000, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);


    private final int code;
    private final String message;
    // httpStatus도 같이 관리하는게 좋지 않을까?
    private final HttpStatus httpStatus;

    ResponseCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static ResponseCode fromCode(int code) {
        return Arrays.stream(ResponseCode.values())
                .filter(c -> c.code == code)
                .findFirst()
                .orElse(UN_KNOWN_ERROR);
    }

}
