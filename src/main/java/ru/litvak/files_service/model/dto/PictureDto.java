package ru.litvak.files_service.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class PictureDto {
    private Long id;
    private UUID userId;
    private String fileId;
    private String contentType;
    private Instant uploadedAt;
}
