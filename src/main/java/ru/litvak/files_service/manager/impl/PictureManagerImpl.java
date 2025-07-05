package ru.litvak.files_service.manager.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.litvak.files_service.exception.NotFoundException;
import ru.litvak.files_service.manager.PictureManager;
import ru.litvak.files_service.model.entity.Picture;
import ru.litvak.files_service.repository.PictureRepository;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PictureManagerImpl implements PictureManager {

    private final PictureRepository pictureRepository;

    @Override
    public Picture get(String giftId) {
        return pictureRepository.findByFileId(giftId)
                .orElseThrow(() -> new NotFoundException("Picture with id: %s not found.".formatted(giftId)));
    }

    @Override
    public void save(UUID me, String contentType, String giftId) {
        Picture picture = pictureRepository.findByFileId(giftId)
                .map(a -> {
                    a.setContentType(contentType);
                    a.setUploadedAt(Instant.now());
                    return a;
                })
                .orElseGet(() ->
                        Picture.builder()
                                .userId(me)
                                .fileId(giftId)
                                .contentType(contentType)
                                .build()
                );
        pictureRepository.save(picture);
    }
}
