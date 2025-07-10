package ru.litvak.files_service.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;

public interface PictureService {
    ResponseEntity<byte[]> getGiftPicture(String authHeader, String giftId, SizeType size);

    void addPicture(String authHeader, String giftId, MultipartFile file);

    void deletePicture(String authHeader, String giftId);
}
