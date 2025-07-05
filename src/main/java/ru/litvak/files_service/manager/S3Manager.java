package ru.litvak.files_service.manager;

import org.springframework.web.multipart.MultipartFile;

public interface S3Manager {
    void save(String name, String bucket, MultipartFile file);

    byte[] get(String name, String bucket);

    void delete(String name, String bucket);
}
