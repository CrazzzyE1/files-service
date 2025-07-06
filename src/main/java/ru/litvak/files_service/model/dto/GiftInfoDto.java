package ru.litvak.files_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.litvak.files_service.enumerated.PrivacyLevel;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GiftInfoDto {
    private UUID userId;
    private PrivacyLevel wishListPrivacyLevel;
}
