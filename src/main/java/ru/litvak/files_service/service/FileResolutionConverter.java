package ru.litvak.files_service.service;

import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;

public interface FileResolutionConverter {
    MultipartFile convert(MultipartFile file, SizeType size);
}
