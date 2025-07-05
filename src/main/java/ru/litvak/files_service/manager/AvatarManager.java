package ru.litvak.files_service.manager;

import ru.litvak.files_service.model.entity.Avatar;

import java.util.UUID;

public interface AvatarManager {
    Avatar get(UUID userId);

    void save(UUID me, String contentType);

    void delete(UUID me);
}
