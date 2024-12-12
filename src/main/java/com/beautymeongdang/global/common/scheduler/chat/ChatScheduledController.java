package com.beautymeongdang.global.common.scheduler.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler/chat")
@RequiredArgsConstructor
public class ChatScheduledController {
    private final ChatScheduledService chatScheduledService;

    // 채팅 물리적 삭제 스케줄러 테스트 컨트롤러
//    @GetMapping("/delete-messages")
//    public String triggerDeleteChatMessages() {
//        chatScheduledService.deleteChatMessages();
//        return "Chat messages older than 30 days have been deleted.";
//    }

}
