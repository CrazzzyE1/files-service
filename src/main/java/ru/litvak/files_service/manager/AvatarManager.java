package ru.litvak.files_service.manager;

import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.model.entity.Avatar;

import java.util.UUID;

public interface AvatarManager {
    Avatar get(UUID userId, SizeType size);

    void save(String fileName, SizeType size, UUID me, String contentType);
}
