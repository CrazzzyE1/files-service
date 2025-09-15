package ru.litvak.files_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.integration.UserServiceFacade;
import ru.litvak.files_service.manager.AvatarManager;
import ru.litvak.files_service.manager.PictureConverter;
import ru.litvak.files_service.manager.S3Manager;
import ru.litvak.files_service.mapper.AvatarMapper;
import ru.litvak.files_service.model.dto.AvatarDto;
import ru.litvak.files_service.service.AvatarService;
import ru.litvak.files_service.util.JwtTokenMapper;

import java.util.UUID;

import static ru.litvak.files_service.util.SupportUtil.DEFAULT_CONTENT_TYPE;
import static ru.litvak.files_service.util.SupportUtil.generateName;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    @Value("${minio.bucket.name.avatars}")
    private String bucket;

    private static final String DEFAULT_AVATAR_NAME = "default_avatar";
    private static final String DELETE_USER_AVATAR_NAME = "delete_user_avatar";

    private final AvatarManager avatarManager;
    private final S3Manager s3Manager;
    private final AvatarMapper avatarMapper;
    private final PictureConverter pictureConverter;
    private final UserServiceFacade userServiceFacade;

    @Override
    public ResponseEntity<byte[]> loadAvatar(UUID userId, SizeType size) {
        AvatarDto meta = avatarMapper.toDto(avatarManager.get(userId));
        boolean isUserActive = userServiceFacade.isUserActive(userId);
        String fileName;

        if (isUserActive) {
            fileName = meta != null ?
                    generateName(String.valueOf(userId), size) : generateName(DEFAULT_AVATAR_NAME, size);
        } else {
            fileName = generateName(DELETE_USER_AVATAR_NAME, size);
        }

        String contentType = meta != null ? meta.getContentType() : DEFAULT_CONTENT_TYPE;
        byte[] data = s3Manager.get(fileName, bucket);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @Override
    public ResponseEntity<byte[]> loadAvatar(String authHeader, SizeType size) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        return loadAvatar(me, size);
    }

    @Override
    @Transactional
    public void saveAvatar(String authHeader, MultipartFile file) {
        UUID userId = JwtTokenMapper.getUserId(authHeader);
        avatarManager.save(userId, DEFAULT_CONTENT_TYPE);
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(String.valueOf(userId), size);
            s3Manager.save(fileName, bucket, pictureConverter.resize(file, size, true));
        }
    }

    @Override
    @Transactional
    public void deleteAvatar(String authHeader) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(String.valueOf(me), size);
            s3Manager.delete(fileName, bucket);
        }
        avatarManager.delete(me);
    }
}