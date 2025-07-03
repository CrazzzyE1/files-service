package ru.litvak.files_service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;

import java.util.UUID;

public interface AvatarService {

    ResponseEntity<byte[]> loadAvatar(UUID userId, SizeType size);

    void saveAvatar(String authHeader, MultipartFile file);
}
