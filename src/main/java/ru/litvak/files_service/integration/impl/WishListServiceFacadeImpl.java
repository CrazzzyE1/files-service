package ru.litvak.files_service.integration.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.litvak.files_service.integration.WishListServiceFacade;
import ru.litvak.files_service.integration.request.RelationRequest;
import ru.litvak.files_service.integration.response.RelationsDto;

import java.util.UUID;

@Component
public class WishListServiceFacadeImpl implements WishListServiceFacade {

    private final RestTemplate restTemplate;

    public WishListServiceFacadeImpl(@Qualifier(value = "wishlist-service")RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

//    @Override
//    public RelationsDto getRelations(UUID me, UUID userId) {
//        return restTemplate.postForObject(
//                "/api/v1/profiles/relations",
//                new RelationRequest(me, userId),
//                RelationsDto.class
//        );
//    }
}
