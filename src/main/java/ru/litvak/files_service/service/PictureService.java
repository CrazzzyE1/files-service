package ru.litvak.files_service.service;

import org.springframework.http.ResponseEntity;

public interface PictureService {
    ResponseEntity<byte[]> getGiftPicture(String authHeader, String giftId);
}
