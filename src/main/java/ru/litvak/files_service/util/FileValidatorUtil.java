package ru.litvak.files_service.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

public class FileValidatorUtil {

    public static void validateContentType(String contentType) {
        List<String> allowedContentTypes = Arrays.asList("image/jpeg", "image/png", "image/webp");
        if (!allowedContentTypes.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG, PNG and WebP images are allowed");
        }
    }
}
