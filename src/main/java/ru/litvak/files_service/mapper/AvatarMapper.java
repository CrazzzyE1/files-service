package ru.litvak.files_service.mapper;

import org.mapstruct.Mapper;
import ru.litvak.files_service.model.dto.AvatarDto;
import ru.litvak.files_service.model.entity.Avatar;

@Mapper(componentModel = "spring")
public interface AvatarMapper {

    AvatarDto toDto(Avatar entity);
}
