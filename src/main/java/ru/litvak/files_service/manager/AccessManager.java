package ru.litvak.files_service.manager;

import java.util.UUID;

public interface AccessManager {
    boolean readPictureAccess(UUID me, String giftId);

    boolean writePictureAccess(UUID me, String giftId);
}
