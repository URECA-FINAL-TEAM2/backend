package com.beautymeongdang.global.common.scheduler.dog;

import com.beautymeongdang.domain.dog.entity.Dog;
import com.beautymeongdang.domain.dog.repository.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.beautymeongdang.infra.s3.FileStore;
import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DogScheduledService {

    private final DogRepository dogRepository;
    private final FileStore fileStore;

    // 반려견 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deleteDog() {
        List<Dog> dogs = dogRepository.findAllByIsDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));

        dogs.forEach(dog -> {
            if (dog.getProfileImage() != null) {
                fileStore.deleteFile(dog.getProfileImage());
            }
            dogRepository.delete(dog);
        });
    }
}
