package ru.litvak.files_service.manager;

import java.util.UUID;

public interface AccessManager {
    boolean checkImageAccess(UUID me, String giftId);
}
