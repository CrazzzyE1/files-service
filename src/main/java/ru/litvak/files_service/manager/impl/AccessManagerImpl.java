package ru.litvak.files_service.manager.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.litvak.files_service.integration.UserServiceFacade;
import ru.litvak.files_service.integration.WishListServiceFacade;
import ru.litvak.files_service.manager.AccessManager;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessManagerImpl implements AccessManager {

    private final UserServiceFacade userServiceFacade;
    private final WishListServiceFacade wishListServiceFacade;

    @Override
    public boolean checkImageAccess(UUID me, String giftId) {
        // FIXME 05.07.2025:13:39: Add logic
//        UUID friendId = null;
//
//        userServiceFacade.getRelations(me, friendId);
//        return false;
        return true;
    }
}
