package ru.litvak.files_service.integration.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RelationRequest {
    @NotNull
    private UUID me;
    @NotNull
    private UUID friend;
}
