package ru.litvak.files_service.exception.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ValidationErrorDto {
    private String message;
    private List<ValidationErrorFieldDto> fields;
}
