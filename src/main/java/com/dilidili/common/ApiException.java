package com.dilidili.common;

import lombok.Getter;

/**
 * API 自定义异常
 */
@Getter
public class ApiException extends RuntimeException {
    private final int code;

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
