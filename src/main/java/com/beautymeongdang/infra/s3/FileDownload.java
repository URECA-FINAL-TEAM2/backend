package com.beautymeongdang.infra.s3;

import com.amazonaws.services.s3.AmazonS3Client;

import com.beautymeongdang.global.exception.handler.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileDownload {

    @Value("${file.dir}")
    private String fileDir;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3Client amazonS3Client;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public void validateFileExistsAtUrl(String resourcePath) {

        if (!amazonS3Client.doesObjectExist(bucketName, resourcePath)) {
            throw NotFoundException.entityNotFound("파일");
        }
    }
}