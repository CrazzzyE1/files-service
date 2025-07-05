package ru.litvak.files_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.litvak.files_service.service.PictureService;
import ru.litvak.files_service.util.JwtTokenMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PictureServiceImpl implements PictureService {
    @Override
    public ResponseEntity<byte[]> getGiftPicture(String authHeader, String giftId) {
        UUID me = JwtTokenMapper.getUserId(authHeader);
        // TODO 04.07.2025:0:32: add logic
        return null;
    }
}
