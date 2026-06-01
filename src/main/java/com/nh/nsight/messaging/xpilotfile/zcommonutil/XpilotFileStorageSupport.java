package com.nh.nsight.messaging.xpilotfile.zcommonutil;

import com.nh.nsight.messaging.config.FileStorageProperties;
import com.nh.nsight.messaging.xpilotfile.dc.filedc.dto.FileDDTO;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class XpilotFileStorageSupport {

    private final FileStorageProperties properties;

    public XpilotFileStorageSupport(FileStorageProperties properties) {
        this.properties = properties;
    }

    public Path storageRoot() {
        return Paths.get(properties.getStoragePath()).toAbsolutePath().normalize();
    }

    public Path resolvePhysicalPath(FileDDTO file) {
        return storageRoot().resolve(file.getStoragePath()).normalize();
    }

    public String formatSizeLabel(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    public String sanitizeFilename(String filename) {
        return filename.replace("\\", "_").replace("/", "_").trim();
    }

    public String extractExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index < 0) {
            return "bin";
        }
        return filename.substring(index + 1).toLowerCase();
    }
}
