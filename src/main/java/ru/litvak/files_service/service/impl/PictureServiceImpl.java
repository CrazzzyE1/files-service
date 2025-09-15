package ru.litvak.files_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.manager.AccessManager;
import ru.litvak.files_service.manager.PictureConverter;
import ru.litvak.files_service.manager.PictureManager;
import ru.litvak.files_service.manager.S3Manager;
import ru.litvak.files_service.mapper.PictureMapper;
import ru.litvak.files_service.model.dto.PictureDto;
import ru.litvak.files_service.model.request.ClonePictureRequest;
import ru.litvak.files_service.service.PictureService;
import ru.litvak.files_service.util.JwtTokenMapper;

import java.util.UUID;

import static ru.litvak.files_service.util.SupportUtil.DEFAULT_CONTENT_TYPE;
import static ru.litvak.files_service.util.SupportUtil.generateName;

@Slf4j
@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    @Value("${minio.bucket.name.pictures}")
    private String bucket;

    private static final String DEFAULT_PICTURE_NAME = "default_picture";

    private final PictureManager pictureManager;
    private final PictureMapper pictureMapper;
    private final AccessManager accessManager;
    private final S3Manager s3Manager;
    private final PictureConverter pictureConverter;

    @Override
    public ResponseEntity<byte[]> getGiftPicture(String authHeader, String giftId, SizeType size) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        PictureDto meta = pictureMapper.toDto(pictureManager.get(giftId));
        boolean access = accessManager.readPictureAccess(me, giftId);
        if (!access) {
            log.warn("Access denied");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String fileName = meta != null ?
                generateName(String.valueOf(giftId), size) : generateName(DEFAULT_PICTURE_NAME, size);
        String contentType = meta != null ? meta.getContentType() : DEFAULT_CONTENT_TYPE;

        byte[] data = s3Manager.get(fileName, bucket);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @Override
    @Transactional
    public void addPicture(String authHeader, String giftId, MultipartFile file) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        boolean access = accessManager.writePictureAccess(me, giftId);
        if (!access) {
            log.warn("Add picture. Access denied.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        pictureManager.save(me, DEFAULT_CONTENT_TYPE, giftId);
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(String.valueOf(giftId), size);
            s3Manager.save(fileName, bucket, pictureConverter.resize(file, size, false));
        }
    }

    @Override
    @Transactional
    public void deletePicture(String authHeader, String giftId) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        boolean access = accessManager.writePictureAccess(me, giftId);
        if (!access) {
            log.warn("Delete picture. Access denied.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(String.valueOf(giftId), size);
            s3Manager.delete(fileName, bucket);
        }
        pictureManager.delete(giftId);
    }

    @Override
    public void clonePicture(ClonePictureRequest request) {
        if (!pictureManager.exist(request.getGiftId())) {
         return;
        }
        pictureManager.save(request.getId(), DEFAULT_CONTENT_TYPE, request.getNewId());
        for (SizeType size : SizeType.values()) {
            String source = generateName(String.valueOf(request.getGiftId()), size);
            String target = generateName(String.valueOf(request.getNewId()), size);
            s3Manager.clonePicture(source, target, bucket);
        }
    }
}
