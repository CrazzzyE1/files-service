package ru.litvak.files_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.service.FileResolutionConverter;

@Service
@RequiredArgsConstructor
public class FileResolutionConverterImpl implements FileResolutionConverter {
    @Override
    public MultipartFile convert(MultipartFile file, SizeType size) {
        // FIXME 05.07.2025:11:37: Add convert logic
        return file;
    }
}
