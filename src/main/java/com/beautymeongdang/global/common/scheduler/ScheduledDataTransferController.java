package com.beautymeongdang.global.common.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class ScheduledDataTransferController {
    private final ScheduledDataTransferService scheduledDataTransferService;

    // 채팅 물리적 삭제 스케줄러 테스트 컨트롤러
//    @GetMapping("/delete-chat-messages")
//    public String triggerDeleteChatMessages() {
//        scheduledDataTransferService.deleteChatMessages();
//        return "Chat messages older than 30 days have been deleted.";
//    }

}
