package ru.litvak.files_service.integration;

import ru.litvak.files_service.model.dto.GiftInfoDto;

public interface WishListServiceFacade {
    GiftInfoDto getGiftInfo(String giftId);
}
