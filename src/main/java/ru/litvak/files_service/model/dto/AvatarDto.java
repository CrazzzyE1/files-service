package ru.litvak.files_service.model.dto;

import lombok.Getter;
import lombok.Setter;
import ru.litvak.files_service.enumerated.SizeType;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class AvatarDto {
    private Long id;
    private UUID userId;
    private String fileName;
    private String contentType;
    private String s3Key;
    private Instant uploadedAt;
    private SizeType size;
}
