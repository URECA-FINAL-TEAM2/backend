package com.beautymeongdang.global.common.scheduler.dog;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduler/dog")
@RequiredArgsConstructor
public class DogScheduledController {

    private final DogScheduledService dogScheduledService;

    @GetMapping("/delete")
    public String deleteDog() {
        dogScheduledService.deleteDog();
        return "Delete DogScheduled Success";
    }



}
