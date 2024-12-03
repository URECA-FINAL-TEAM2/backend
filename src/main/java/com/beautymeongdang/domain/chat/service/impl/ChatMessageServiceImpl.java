package com.beautymeongdang.domain.chat.service.impl;

import com.beautymeongdang.domain.chat.service.ChatMessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageServiceImpl extends ChatMessageService {
}
