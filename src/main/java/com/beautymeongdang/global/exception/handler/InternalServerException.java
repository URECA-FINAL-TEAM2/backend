package com.beautymeongdang.global.exception.handler;

import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {
    static private final String SERVER_ERROR = "서버 처리 중 오류가 발생했습니다. %s가(이) 원인입니다.";
    
    public InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static InternalServerException error(String target) {
        return new InternalServerException(String.format(SERVER_ERROR, target));
    }
}