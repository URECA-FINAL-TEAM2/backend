package com.beautymeongdang.global.exception.handler;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    static private final String BAD_REQUEST = "잘못된 요청입니다. %s가(이) 유효하지 않습니다.";

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public static BadRequestException invalidRequest(String target) {
        return new BadRequestException(String.format(BAD_REQUEST, target));
    }
}