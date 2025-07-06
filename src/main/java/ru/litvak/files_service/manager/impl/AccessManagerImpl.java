package ru.litvak.files_service.manager.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.litvak.files_service.enumerated.PrivacyLevel;
import ru.litvak.files_service.integration.UserServiceFacade;
import ru.litvak.files_service.integration.WishListServiceFacade;
import ru.litvak.files_service.integration.response.RelationsDto;
import ru.litvak.files_service.manager.AccessManager;
import ru.litvak.files_service.model.dto.GiftInfoDto;

import java.util.UUID;

import static ru.litvak.files_service.enumerated.PrivacyLevel.*;

@Component
@RequiredArgsConstructor
public class AccessManagerImpl implements AccessManager {

    private final UserServiceFacade userServiceFacade;
    private final WishListServiceFacade wishListServiceFacade;

    @Override
    public boolean writePictureAccess(UUID me, String giftId) {
        GiftInfoDto giftInfo = wishListServiceFacade.getGiftInfo(giftId);
        return me.equals(giftInfo.getUserId());
    }

    @Override
    public boolean readPictureAccess(UUID me, String giftId) {
        GiftInfoDto giftInfo = wishListServiceFacade.getGiftInfo(giftId);
        UUID userId = giftInfo.getUserId();

        if (me.equals(userId)) {
            return true;
        }

        RelationsDto relations = userServiceFacade.getRelations(me, userId);
        PrivacyLevel userPrivacyLevel = relations.getPrivacyLevel();
        PrivacyLevel wishListPrivacyLevel = giftInfo.getWishListPrivacyLevel();

        if (isPrivate(userPrivacyLevel, wishListPrivacyLevel)) {
            return false;
        }

        return hasAccess(userPrivacyLevel, wishListPrivacyLevel, relations.isFriends());
    }

    private boolean isPrivate(PrivacyLevel userPrivacy, PrivacyLevel wishListPrivacy) {
        return PRIVATE.equals(userPrivacy) || PRIVATE.equals(wishListPrivacy);
    }

    private boolean hasAccess(PrivacyLevel userPrivacy, PrivacyLevel wishListPrivacy, boolean isFriends) {
        if (isPublic(userPrivacy) && isPublic(wishListPrivacy)) {
            return true;
        }

        if (isFriends) {
            return (isFriendsOnly(userPrivacy) && isNotPrivate(wishListPrivacy)) ||
                    (isPublic(userPrivacy) && isFriendsOnly(wishListPrivacy));
        }

        return false;
    }

    private boolean isPublic(PrivacyLevel level) {
        return PUBLIC.equals(level);
    }

    private boolean isFriendsOnly(PrivacyLevel level) {
        return FRIENDS_ONLY.equals(level);
    }

    private boolean isNotPrivate(PrivacyLevel level) {
        return !PRIVATE.equals(level);
    }
}
