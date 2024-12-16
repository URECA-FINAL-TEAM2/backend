package com.beautymeongdang.domain.chat.config;

import org.springframework.messaging.converter.MessageConverter;
import com.beautymeongdang.domain.chat.handler.StompHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;


@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private final StompHandler stompHandler;
    private final ObjectMapper objectMapper;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("Configuring message broker...");
        config.enableSimpleBroker("/sub");  // 구독 prefix
        config.setApplicationDestinationPrefixes("/pub");  // 발행 prefix
        log.info("Message broker configured with prefix /sub and /pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering STOMP endpoints...");
        registry.addEndpoint("/ws")
                  .setAllowedOriginPatterns("*");
//                .setAllowedOriginPatterns("http://localhost:5173", "https://www.beautymeongdang.com");
//                .withSockJS();
        log.info("STOMP endpoints registered successfully");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    // 메시지 보낼 때 용량 제한
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(20 * 1024 * 1024); // 메시지 크기 제한: 20MB
        registry.setSendBufferSizeLimit(20 * 1024 * 1024); // 버퍼 크기 제한: 20MB
        registry.setSendTimeLimit(20000); // 메시지 보낼 시간 제한: 20초
    }


    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        messageConverters.add(converter);
        return true;
    }

}