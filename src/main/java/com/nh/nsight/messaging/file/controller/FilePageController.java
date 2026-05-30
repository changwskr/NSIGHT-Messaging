package com.nh.nsight.messaging.file.controller;

import com.nh.nsight.messaging.config.FileStorageProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FilePageController {

    private final FileStorageProperties fileStorageProperties;

    public FilePageController(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @GetMapping("/files")
    public String files(Model model) {
        Path basePath = Paths.get(fileStorageProperties.getStoragePath()).toAbsolutePath().normalize();
        model.addAttribute("storageBasePath", basePath.toString());
        model.addAttribute("storageConfiguredPath", fileStorageProperties.getStoragePath());
        model.addAttribute("storagePathPattern", basePath + "/yyyy/MM/dd/{storedName}");
        model.addAttribute("maxFileSizeLabel", formatSize(fileStorageProperties.getMaxFileSizeBytes()));
        model.addAttribute("allowedExtensions", fileStorageProperties.getAllowedExtensions());
        return "files/manage";
    }

    private String formatSize(long bytes) {
        if (bytes < 1024 * 1024) {
            return (bytes / 1024) + " KB";
        }
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
}
