package ru.litvak.files_service.manager.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.litvak.files_service.enumerated.SizeType;
import ru.litvak.files_service.manager.PictureConverter;

import javax.imageio.ImageIO;
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
    public MultipartFile resize(MultipartFile originalFile, SizeType size, boolean isAvatar) {
        try {
            Objects.requireNonNull(originalFile, "Original file cannot be null");
            Objects.requireNonNull(size, "Size type cannot be null");

            String contentType = originalFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are supported");
            }

            BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image file");
            }

            BufferedImage resizedImage = resizeImage(originalImage, size, isAvatar);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            ImageIO.write(resizedImage, "jpeg", os);

            byte[] imageBytes = os.toByteArray();

            return new MultipartFile() {
                @Override
                public String getName() {
                    return originalFile.getName();
                }

                @Override
                public String getOriginalFilename() {
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

    private BufferedImage resizeImage(BufferedImage originalImage, SizeType sizeType, boolean isAvatar) {
        int targetWidth, targetHeight;
        if (isAvatar) {
            switch (sizeType) {
                case SMALL -> {
                    targetWidth = 70;
                    targetHeight = 70;
                }
                case MEDIUM -> {
                    targetWidth = 100;
                    targetHeight = 100;
                }
                case LARGE -> {
                    targetWidth = 150;
                    targetHeight = 150;
                }
                default -> throw new IllegalArgumentException("Unknown size type: " + sizeType);
            }
        } else {
            switch (sizeType) {
                case SMALL -> {
                    targetWidth = 350;
                    targetHeight = 350;
                }
                case MEDIUM -> {
                    targetWidth = 500;
                    targetHeight = 500;
                }
                case LARGE -> {
                    targetWidth = 800;
                    targetHeight = 800;
                }
                default -> throw new IllegalArgumentException("Unknown size type: " + sizeType);
            }
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
}