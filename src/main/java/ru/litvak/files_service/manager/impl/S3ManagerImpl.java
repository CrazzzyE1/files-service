package ru.litvak.files_service.manager.impl;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.manager.S3Manager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ManagerImpl implements S3Manager {

    private final MinioClient minioClient;
    private final Set<String> existingBuckets = ConcurrentHashMap.newKeySet();

    @Override
    public void save(String name, String bucketName, MultipartFile file) {
        try {
            ensureBucketExists(bucketName);
            uploadFile(name, bucketName, file);
        } catch (Exception e) {
            log.error("File save failed: name={}, bucket={}", name, bucketName, e);
            throw new RuntimeException("File save failed: %s".formatted(name), e);
        }
    }

    @Override
    public byte[] get(String name, String bucketName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(name)
                            .build()).readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read the file: %s".formatted(name), e);
        }
    }

    private void ensureBucketExists(String bucketName) throws Exception {
        if (!existingBuckets.contains(bucketName)) {
            synchronized (this) {
                if (!existingBuckets.contains(bucketName)) {
                    checkAndCreateBucket(bucketName);
                    existingBuckets.add(bucketName);
                }
            }
        }
    }

    private void checkAndCreateBucket(String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            log.info("Creating new bucket: {}", bucketName);
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private void uploadFile(String name, String bucketName, MultipartFile file)
            throws IOException, GeneralSecurityException, MinioException {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(name)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        log.debug("File uploaded successfully: {} to bucket {}", name, bucketName);
    }
}
