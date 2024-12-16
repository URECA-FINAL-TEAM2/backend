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

    // 미용사 프로필 삭제 스케줄러
//    @GetMapping("/delete-grooemr")
//    public String deleteGrooemr() {
//        userScheduledService.deleteGroomerProfile();
//        return "Delete groomerProfile Success";
//    }

}
