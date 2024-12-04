package com.beautymeongdang.global.login.service;

import java.util.Map;

public interface  OAuth2Service {
    Map<String, Object> processKakaoLogin(String code);
}
