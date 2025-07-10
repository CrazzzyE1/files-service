package ru.litvak.files_service.manager.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.manager.PictureConverter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PictureConverterImpl implements PictureConverter {

    @Override
    public MultipartFile resize(MultipartFile originalFile, SizeType size) {
        try {
            // Проверка входных параметров
            Objects.requireNonNull(originalFile, "Original file cannot be null");
            Objects.requireNonNull(size, "Size type cannot be null");

            // Проверка формата файла
            String contentType = originalFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are supported");
            }

            BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image file");
            }

            BufferedImage resizedImage = resizeImage(originalImage, size);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            String formatName = getFormatName(contentType);
            if ("webp".equalsIgnoreCase(formatName)) {
                ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {
                    writer.setOutput(ios);
                    writer.write(resizedImage);
                }
            } else {
                ImageIO.write(resizedImage, formatName, os);
            }

            byte[] imageBytes = os.toByteArray();

            return new MultipartFile() {
                @Override
                public String getName() {
                    return originalFile.getName();
                }

                @Override
                public String getOriginalFilename() {
                    if ("webp".equalsIgnoreCase(formatName)) {
                        String name = originalFile.getOriginalFilename();
                        if (name != null) {
                            int dotIndex = name.lastIndexOf('.');
                            if (dotIndex > 0) {
                                return name.substring(0, dotIndex) + ".webp";
                            }
                        }
                    }
                    return originalFile.getOriginalFilename();
                }

                @Override
                public String getContentType() {
                    return originalFile.getContentType();
                }

                @Override
                public boolean isEmpty() {
                    return imageBytes.length == 0;
                }

                @Override
                public long getSize() {
                    return imageBytes.length;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return imageBytes;
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new ByteArrayInputStream(imageBytes);
                }

                @Override
                public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                    new java.io.FileOutputStream(dest).write(imageBytes);
                }
            };

        } catch (IOException e) {
            throw new RuntimeException("Failed to process image", e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, SizeType sizeType) {
        int targetWidth, targetHeight;

        switch (sizeType) {
            case SMALL -> {
                targetWidth = 100;
                targetHeight = 100;
            }
            case MEDIUM -> {
                targetWidth = 300;
                targetHeight = 300;
            }
            case LARGE -> {
                targetWidth = 800;
                targetHeight = 800;
            }
            default -> throw new IllegalArgumentException("Unknown size type: " + sizeType);
        }

        double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
        if (originalImage.getWidth() > originalImage.getHeight()) {
            targetHeight = (int) (targetWidth / aspectRatio);
        } else {
            targetWidth = (int) (targetHeight * aspectRatio);
        }

        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(resultingImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();

        return outputImage;
    }

    private String getFormatName(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type cannot be null");
        }

        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> "jpeg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new IllegalArgumentException("Unsupported image format: " + contentType);
        };
    }
}