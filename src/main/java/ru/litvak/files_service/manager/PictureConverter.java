package ru.litvak.files_service.manager;

import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;

public interface PictureConverter {
    MultipartFile resize(MultipartFile originalFile, SizeType size, boolean isAvatar);
}
