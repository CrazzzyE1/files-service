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
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.manager.AccessManager;
import ru.litvak.files_service.manager.PictureManager;
import ru.litvak.files_service.manager.S3Manager;
import ru.litvak.files_service.mapper.PictureMapper;
import ru.litvak.files_service.model.dto.PictureDto;
import ru.litvak.files_service.service.FileResolutionConverter;
import ru.litvak.files_service.service.PictureService;
import ru.litvak.files_service.util.JwtTokenMapper;

import java.util.UUID;

import static ru.litvak.files_service.util.SupportUtil.generateName;

@Slf4j
@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {

    @Value("${minio.bucket.name.pictures}")
    private String bucket;

    private final PictureManager pictureManager;
    private final PictureMapper pictureMapper;
    private final AccessManager accessManager;
    private final S3Manager s3Manager;
    private final FileResolutionConverter fileResolutionConverter;

    @Override
    public ResponseEntity<byte[]> getGiftPicture(String authHeader, String giftId, SizeType size) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        PictureDto dto = pictureMapper.toDto(pictureManager.get(giftId));
        boolean access = accessManager.checkImageAccess(me, giftId);
        if (!access) {
            log.warn("Access denied");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        byte[] data = s3Manager.get(generateName(String.valueOf(giftId), size), bucket);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dto.getContentType()))
                .body(data);
    }

    @Override
    @Transactional
    public void addPicture(String authHeader, String giftId, MultipartFile file) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        boolean access = accessManager.checkImageAccess(me, giftId);
        if (!access) {
            log.warn("Add picture. Access denied.");
            return;
        }
        pictureManager.save(me, file.getContentType(), giftId);
        for (SizeType size : SizeType.values()) {
            String fileName = generateName(String.valueOf(giftId), size);
            s3Manager.save(fileName, bucket, fileResolutionConverter.convert(file, size));
        }
    }
}
