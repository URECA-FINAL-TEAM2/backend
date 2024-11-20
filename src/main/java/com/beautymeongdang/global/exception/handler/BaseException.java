package com.beautymeongdang.global.exception.handler;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public BaseException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}

