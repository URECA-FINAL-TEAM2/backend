package com.beautymeongdang.global.common.scheduler.quote;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler/quote")
@RequiredArgsConstructor
public class QuoteScheduledController {

    private final QuoteScheduledService quoteScheduledService;

    // 견적서 물리적 삭제 스케줄러 테스트 컨트롤러
//    @GetMapping("/delete-quote")
//    public String deleteQuote() {
//        quoteScheduledService.deleteQuote();
//        return "Delete Quote Success";
//    }

    // 견적서 진행상태 변경 스케줄러 테스트 컨트롤러
//    @GetMapping("/update-quote")
//    public String updateQuoteStatus() {
//        quoteScheduledService.updateQuoteStatus();
//        return "Update QuoteStatus Success";
//    }

}
