package com.beautymeongdang.global.common.scheduler.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler/user")
@RequiredArgsConstructor
public class UserScheduledController {

    private final UserScheduledService userScheduledService;

    // 미용사 프로필 물리적 삭제 스케줄러 테스트 컨트롤러
//    @GetMapping("/delete-groomer")
//    public String deleteGroomer() {
//        userScheduledService.deleteGroomerProfile();
//        return "Delete groomerProfile Success";
//    }

}
