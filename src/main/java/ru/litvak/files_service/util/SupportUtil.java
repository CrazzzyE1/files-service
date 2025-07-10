package ru.litvak.files_service.util;

import ru.litvak.files_service.enumerated.SizeType;

public class SupportUtil {

    public static final String DEFAULT_CONTENT_TYPE = "image/png";

    public static String generateName(String baseName, SizeType sizeType) {
        return String.format("%s_%s", baseName, sizeType.name().toLowerCase());
    }
}
