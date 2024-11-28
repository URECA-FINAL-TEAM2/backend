package com.beautymeongdang.domain.login.service;

public interface OAuth2ResponseService {

    String getProvider();// 제공자 (Ex. kakao, google, ...)
    String getProviderId();
    String getEmail();
    String getName();     // 사용자 실명 (설정한 이름)
    String getProfileImage();

}