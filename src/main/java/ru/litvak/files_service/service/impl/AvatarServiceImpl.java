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
import ru.litvak.files_service.service.FileResolutionConverter;
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
    private final FileResolutionConverter fileResolutionConverter;

    @Override
    public ResponseEntity<byte[]> loadAvatar(UUID userId, SizeType size) {
        AvatarDto meta = avatarMapper.toDto(avatarManager.get(userId));
        String fileName = meta != null ?
                generateName(String.valueOf(meta.getUserId()), size) : generateName(DEFAULT_AVATAR_NAME, size);
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
        avatarManager.save(userId, file.getContentType());
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(String.valueOf(userId), size);
            s3Manager.save(fileName, AVATARS_BUCKET_NAME, fileResolutionConverter.convert(file, size));
        }
    }

    @Override
    public ResponseEntity<byte[]> loadAvatar(String authHeader, SizeType size) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        return loadAvatar(me, size);
    }

    @Override
    @Transactional
    public void deleteAvatar(String authHeader) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(String.valueOf(me), size);
            s3Manager.delete(fileName, AVATARS_BUCKET_NAME);
        }
        avatarManager.delete(me);
    }

    private String generateName(String baseName, SizeType sizeType) {
        return String.format("%s_%s", baseName, sizeType.name().toLowerCase());
    }
}