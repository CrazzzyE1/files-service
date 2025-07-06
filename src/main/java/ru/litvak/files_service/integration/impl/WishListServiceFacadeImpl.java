package ru.litvak.files_service.integration.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.litvak.files_service.integration.WishListServiceFacade;
import ru.litvak.files_service.model.dto.GiftInfoDto;

@Component
public class WishListServiceFacadeImpl implements WishListServiceFacade {

    private final RestTemplate restTemplate;

    public WishListServiceFacadeImpl(@Qualifier(value = "wishlist-service") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GiftInfoDto getGiftInfo(String giftId) {
        return restTemplate.getForObject(
                "http://WISHLIST-SERVICE/api/v1/gifts/{id}/info",
                GiftInfoDto.class,
                giftId
        );
    }
}
