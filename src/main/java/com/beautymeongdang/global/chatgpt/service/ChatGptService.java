package com.beautymeongdang.global.chatgpt.service;

import com.beautymeongdang.global.chatgpt.dto.ChatCompletionDto;
import com.beautymeongdang.global.chatgpt.dto.CompletionDto;

import java.util.List;
import java.util.Map;

public interface ChatGptService {
    List<Map<String, Object>> selectModelList();

    Map<String, Object> isValidModel(String modelName);

    Map<String, Object> selectLegacyPrompt(CompletionDto completionDto);

    Map<String, Object> selectPrompt(ChatCompletionDto chatCompletionDto);
}
