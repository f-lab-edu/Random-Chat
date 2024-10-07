package com.example.ranchat.response;

import java.util.Arrays;

public enum ResponseCode {
    // HTTP_CODE 200
    SUCCESS(2000),

    // HTTP_CODE 204
    ACCEPTED(2041),

    // HTTP_CODE 400
    // InvalidParameterException
    MISSING_REQUIRED_PARAMETER(4001),
    INVALID_PARAMETER(4002),

    // HTTP_CODE 401
    // AuthException
    NO_AUTH_TOKEN(4011),
    INVALID_AUTH_TOKEN(4012),
    EXPIRED_AUTH_TOKEN(4013),
    FAILED_LOGIN(4014),
    DUPLICATED_LOGIN(4015),
    INVALID_VERIFICATION_CODE(4016),
    INVALID_DEVICE_TOKEN(4017),

    // HTTP_CODE 403
    // PermissionDeniedException
    NOT_ALLOWED(4031),
    NOT_ADMIN_USER(4032),

    NOT_FOUND_USER(4033),

    UN_KNOWN_ERROR(5000);

    //...

    private final int code;

    ResponseCode(int c) {
        this.code = c;
    }

    public static ResponseCode getName(int code) {
        return Arrays.stream(ResponseCode.values()).filter(c -> c.code == code).findFirst().orElse(null);
    }

    public int getCode() {
        return this.code;
    }

    public String toString() {
        switch (this) {
            case SUCCESS -> {
                return "OK";
            }
            //...
            default -> {
                return "Unhandled error";
            }
        }
    }
}
