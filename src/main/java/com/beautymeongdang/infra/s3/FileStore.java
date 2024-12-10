package com.beautymeongdang.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.beautymeongdang.global.common.entity.UploadedFile;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


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
    public static final String SHOP_LOGO= "매장 로고 이미지/";
    public static final String CHAT_IMAGES = "채팅 이미지/";


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

    // 단일 파일 삭제
    public void deleteFile(String fileUrl) {
        try {
            String fileName = extractFileKeyFromUrl(fileUrl);
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        } catch (Exception e) {
            throw InternalServerException.error("파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 다중 파일 삭제
    public void deleteFiles(List<String> fileUrls) {
        try {
            if (fileUrls == null || fileUrls.isEmpty()) {
                return;
            }

            List<String> keys = fileUrls.stream()
                    .map(this::extractFileKeyFromUrl)
                    .collect(Collectors.toList());

            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                    .withKeys(keys.stream()
                            .map(DeleteObjectsRequest.KeyVersion::new)
                            .collect(Collectors.toList()));

            amazonS3Client.deleteObjects(deleteObjectsRequest);
        } catch (Exception e) {
            throw InternalServerException.error("파일 일괄 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // S3 URL에서 파일 키(경로+파일명) 추출
    private String extractFileKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            String decodedPath = java.net.URLDecoder.decode(path, "UTF-8");
            return decodedPath.startsWith("/") ? decodedPath.substring(1) : decodedPath;
        } catch (Exception e) {
            throw InternalServerException.error("파일 URL 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // 채팅 이미지 처리
    public UploadedFile storeBase64File(String base64Image, String directory) {
        try {
            // Base64 데이터와 타입 추출
            String[] parts = base64Image.split(",");
            String base64Header = parts[0];
            String base64Data = parts.length > 1 ? parts[1] : parts[0];

            // 이미지 타입 추출 (예: jpeg, png, gif 등)
            String contentType = "image/png";
            String extension = "png";

            if (base64Header.contains("image/")) {
                contentType = base64Header.substring(base64Header.indexOf("image/"), base64Header.indexOf(";base64"));
                extension = contentType.split("/")[1];
            }

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            String fileName = UUID.randomUUID() + "." + extension;
            String storeFileName = directory + fileName;

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(imageBytes.length);

            try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                amazonS3Client.putObject(bucketName, storeFileName, inputStream, objectMetadata);
            }

            // UploadedFile 생성 및 URL 설정
            UploadedFile uploadedFile = new UploadedFile(fileName, storeFileName);
            uploadedFile.setFileUrl(amazonS3Client.getUrl(bucketName, storeFileName).toString());

            return uploadedFile;

        } catch (Exception e) {
            throw new InternalServerException("이미지 업로드에 실패했습니다: " + e.getMessage());
        }
    }

}