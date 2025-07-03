package ru.litvak.files_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.manager.AvatarManager;
import ru.litvak.files_service.manager.S3Manager;
import ru.litvak.files_service.mapper.AvatarMapper;
import ru.litvak.files_service.model.dto.AvatarDto;
import ru.litvak.files_service.service.AvatarService;
import ru.litvak.files_service.util.JwtTokenMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private static final String AVATARS_BUCKET_NAME = "avatars";
    private static final String DEFAULT_AVATAR_NAME = "default_avatar";
    private static final String DEFAULT_CONTENT_TYPE = "image/png";

    private final AvatarManager avatarManager;
    private final S3Manager s3Manager;
    private final AvatarMapper avatarMapper;

    @Override
    public ResponseEntity<byte[]> loadAvatar(UUID userId, SizeType size) {
        AvatarDto meta = avatarMapper.toDto(avatarManager.get(userId, size));

        String fileName = meta != null ? meta.getFileName() : generateName(DEFAULT_AVATAR_NAME, size);
        String contentType = meta != null ? meta.getContentType() : DEFAULT_CONTENT_TYPE;

        byte[] data = s3Manager.get(fileName, AVATARS_BUCKET_NAME);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @Override
    @Transactional
    public void saveAvatar(String authHeader, MultipartFile file) {
        UUID userId = JwtTokenMapper.getUserId(authHeader);
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(userId.toString(), size);
            avatarManager.save(fileName, size, userId, file.getContentType());
            s3Manager.save(fileName, AVATARS_BUCKET_NAME, file);
        }
    }

    @Override
    public ResponseEntity<byte[]> loadAvatar(String authHeader, SizeType size) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        return loadAvatar(me, size);
    }

    private String generateName(String baseName, SizeType sizeType) {
        return String.format("%s_%s", baseName, sizeType.name().toLowerCase());
    }
}