package com.twintech.shl_tyar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.storage.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.storage.allowed-extensions:jpg,jpeg,png,pdf}")
    private String allowedExtensions;

    @Value("${app.storage.max-file-size-mb:10}")
    private long maxFileSizeMb;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "application/pdf"
    );

    public String storeFile(MultipartFile file, String subDirectory) {
        validateFile(file);

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, subDirectory);
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // Store file
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", targetLocation);
            return subDirectory + "/" + uniqueFilename;

        } catch (IOException ex) {
            log.error("Could not store file: {}", ex.getMessage());
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Check file size
        long fileSizeInMb = file.getSize() / (1024 * 1024);
        if (fileSizeInMb > maxFileSizeMb) {
            throw new RuntimeException("File size exceeds maximum allowed size of " + maxFileSizeMb + "MB");
        }

        // Check MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new RuntimeException("File type not allowed. Allowed types: " + ALLOWED_MIME_TYPES);
        }

        // Check file extension
        String filename = file.getOriginalFilename();
        if (filename == null || !isValidExtension(filename)) {
            throw new RuntimeException("File extension not allowed. Allowed extensions: " + allowedExtensions);
        }
    }

    private boolean isValidExtension(String filename) {
        String extension = getFileExtension(filename);
        List<String> allowedExtensionsList = Arrays.asList(allowedExtensions.split(","));
        return allowedExtensionsList.contains(extension.toLowerCase());
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
            log.info("File deleted successfully: {}", path);
        } catch (IOException ex) {
            log.error("Could not delete file: {}", ex.getMessage());
        }
    }
}