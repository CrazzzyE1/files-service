package ru.litvak.files_service.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.manager.AvatarManager;
import ru.litvak.files_service.model.entity.Avatar;
import ru.litvak.files_service.repository.AvatarRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarManagerImpl implements AvatarManager {

    private final AvatarRepository avatarRepository;

    @Override
    public Avatar get(UUID userId, SizeType size) {
        Optional<Avatar> optionalAvatar = avatarRepository.findByUserIdAndSize(userId, size);
        if (optionalAvatar.isEmpty()) {
            log.info("Avatar with id {} not found.", userId);
            return null;
        }
        return optionalAvatar.get();
    }

    @Override
    public void save(String fileName, SizeType size, UUID me, String contentType) {
        Avatar avatar = avatarRepository.findByUserIdAndSize(me, size)
                .map(a -> {
                    a.setFileName(fileName);
                    a.setContentType(contentType);
                    a.setUploadedAt(Instant.now());
                    return a;
                })
                .orElseGet(() ->
                        Avatar.builder()
                                .userId(me)
                                .fileName(fileName)
                                .contentType(contentType)
                                .size(size)
                                .build()
                );
        avatarRepository.save(avatar);
    }
}
