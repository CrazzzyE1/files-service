package ru.litvak.files_service.manager;

import jakarta.validation.constraints.NotNull;
import ru.litvak.files_service.model.entity.Picture;

import java.util.UUID;

public interface PictureManager {
    Picture get(String id);

    void save(UUID me, String contentType, String id);

    void delete(String id);

    boolean exist(@NotNull String giftId);
}
