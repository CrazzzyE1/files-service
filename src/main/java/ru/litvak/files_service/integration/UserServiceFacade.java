package ru.litvak.files_service.integration;

import ru.litvak.files_service.integration.response.RelationsDto;

import java.util.UUID;

public interface UserServiceFacade {

    RelationsDto getRelations(UUID me, UUID userId);
}
