package com.beautymeongdang.global.chatgpt.service;

import com.beautymeongdang.global.chatgpt.dto.CreateChatCompletionDto;
import com.beautymeongdang.global.chatgpt.dto.CreateCompletionDto;

import java.util.List;
import java.util.Map;

public interface ChatGptService {
    List<Map<String, Object>> selectModelList();

    Map<String, Object> isValidModel(String modelName);

    Map<String, Object> selectLegacyPrompt(CreateCompletionDto completionDto);

    Map<String, Object> selectPrompt(CreateChatCompletionDto chatCompletionDto);
}
