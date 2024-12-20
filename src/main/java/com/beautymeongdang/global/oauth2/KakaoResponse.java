package com.beautymeongdang.global.oauth2;

import com.beautymeongdang.global.login.service.OAuth2ResponseService;

import java.util.Map;

public class KakaoResponse implements OAuth2ResponseService {

    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    @SuppressWarnings("unchecked")
    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        this.profile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        // 이메일은 kakao_account 내부에 있음
        return kakaoAccount.get("email") != null ?
                kakaoAccount.get("email").toString() : null;
    }

    @Override
    public String getName() {
        // 이름은 profile 내부에 있음
        return profile.get("nickname") != null ?
                profile.get("nickname").toString() : null;
    }

    @Override
    public String getProfileImage() {
        return profile != null && profile.get("profile_image_url") != null ?
                profile.get("profile_image_url").toString() : null;
    }
}