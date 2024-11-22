package com.beautymeongdang.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.beautymeongdang.global.common.entity.UploadedFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.beautymeongdang.global.exception.handler.BadRequestException;
import com.beautymeongdang.global.exception.handler.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileStore {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final int FILE_COUNT = 10;
    public static final String USER_PROFILE = "회원 프로필 이미지/";
    public static final String DOG_PROFILE= "반려견 프로필 이미지/";
    public static final String QUOTE_REQUEST= "견적서 요청 이미지/";
    public static final String GROOMER_PORTFOLIO= "미용사 포트폴리오 이미지/";
    public static final String REVIEWS= "리뷰 이미지/";

    private final AmazonS3Client amazonS3Client;

    private UploadedFile convertFile(MultipartFile multipartFile, String directory) {
        String uploadedFilename = multipartFile.getOriginalFilename();
        String serverFileName = createStoreFileName(uploadedFilename, directory);
        return new UploadedFile(uploadedFilename, serverFileName);
    }

    // 파일을 S3에 저장
    private String storeFile(MultipartFile multipartFile, String storeFileName) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(bucketName, storeFileName, inputStream, objectMetadata);
        } catch (IOException e) {
            throw InternalServerException.error("파일 업로드");
        }

        return amazonS3Client.getUrl(bucketName, storeFileName).toString();
    }

    public List<UploadedFile> storeFiles(List<MultipartFile> multipartFiles,String directory) {

        validateFileUploadCount(multipartFiles);

        List<UploadedFile> uploadFiles = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            UploadedFile uploadFile = convertFile(multipartFile,directory);
            String storeFileName = uploadFile.getServerFileName();
            String fileUrl = storeFile(multipartFile, storeFileName);
            uploadFile.setFileUrl(fileUrl);
            uploadFiles.add(uploadFile);
        }

        return uploadFiles;
    }

    private String createStoreFileName(String originalFilename, String directory) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return directory + uuid + "." + ext;
    }

    // 확장자 추출 메소드
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private void validateFileUploadCount(List<MultipartFile> multipartFiles) {
        if (multipartFiles.size() > FILE_COUNT) {
            throw BadRequestException.invalidRequest("파일 개수는 " + FILE_COUNT + "개를 초과할 수 없습니다");
        }
    }
}