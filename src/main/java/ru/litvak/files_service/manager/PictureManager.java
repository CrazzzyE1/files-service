package ru.litvak.files_service.manager;

import ru.litvak.files_service.model.entity.Picture;

import java.util.UUID;

public interface PictureManager {
    Picture get(String id);

    void save(UUID me, String contentType, String id);

    void delete(String id);
}
