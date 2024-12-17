package com.beautymeongdang.global.auth;

import com.beautymeongdang.domain.user.entity.User;
import com.beautymeongdang.domain.user.repository.UserRepository;
import com.beautymeongdang.global.login.service.impl.CustomOAuth2UserServiceImpl;
import com.beautymeongdang.support.fixture.UserTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OAuth2LoginTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private CustomOAuth2UserServiceImpl oAuth2UserService;

    @Test
    @DisplayName("Google OAuth2 로그인 성공 테스트")
    void googleLoginSuccess() throws Exception {
        // given
        User testUser = UserTestDataFactory.createTestUser();
        OAuth2User oauth2User = UserTestDataFactory.createOAuth2User();
        when(oAuth2UserService.loadUser(any())).thenReturn(oauth2User);

        // when
        ResultActions result = mockMvc.perform(get("/login/oauth2/code/google")
                .with(oauth2Login()
                        .oauth2User(oauth2User)));

        // then
        result.andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andDo(print());
    }


    @Test
    @DisplayName("신규 사용자 역할 선택 페이지 리다이렉트 테스트")
    void newUserRedirectTest() throws Exception {
        // given
        User testUser = UserTestDataFactory.createTestUser();
        OAuth2User oauth2User = UserTestDataFactory.createOAuth2User();
        when(oAuth2UserService.loadUser(any())).thenReturn(oauth2User);

        // when
        ResultActions result = mockMvc.perform(get("/login/oauth2/code/google")
                .with(oauth2Login()
                        .oauth2User(oauth2User)));

        // then
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/select-role"))
                .andDo(print());
    }
}