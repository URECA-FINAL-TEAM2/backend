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
    private static final String DEFAULT_DOG_PROFILE_IMAGE = "https://s3-beauty-meongdang.s3.ap-northeast-2.amazonaws.com/%EB%B0%98%EB%A0%A7%EA%B2%AC+%ED%94%84%EB%A1%9C%ED%95%84+%EC%9D%B4%EB%AF%B8%EC%A7%80/%EB%B0%98%EB%A0%A7%EA%B2%AC%ED%94%84%EB%A1%9C%ED%95%84%EA%B8%B0%EB%B3%B8%EC%9D%B4%EB%AF%B8%EC%A7%80.jpg";


    // 반려견 물리적 삭제 스케줄러
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deleteDog() {
        List<Dog> dogs = dogRepository.findAllByIsDeletedAndUpdatedAt(LocalDateTime.now().minusDays(30));

        dogs.forEach(dog -> {
            if (dog.getProfileImage() != null && !dog.getProfileImage().equals(DEFAULT_DOG_PROFILE_IMAGE)) {
                fileStore.deleteFile(dog.getProfileImage());
            }
            dogRepository.delete(dog);
        });
    }
}
