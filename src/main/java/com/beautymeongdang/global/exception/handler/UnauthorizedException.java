package com.beautymeongdang.global.exception.handler;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    static private final String INVALID_ACCESS = "액세스가 거부되었습니다. %s";

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }

    public static UnauthorizedException invalidAccess(String message) {
        return new UnauthorizedException(String.format(INVALID_ACCESS, message));
    }
}