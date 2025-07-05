package ru.litvak.files_service.mapper;

import org.mapstruct.Mapper;
import ru.litvak.files_service.model.dto.PictureDto;
import ru.litvak.files_service.model.entity.Picture;

@Mapper(componentModel = "spring")
public interface PictureMapper {

    PictureDto toDto(Picture entity);
}
