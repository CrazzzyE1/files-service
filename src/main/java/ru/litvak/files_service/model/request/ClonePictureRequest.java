package ru.litvak.files_service.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ClonePictureRequest {
    @NotNull
    UUID id;
    @NotNull
    private String giftId;
    @NotNull
    private String newId;
}
