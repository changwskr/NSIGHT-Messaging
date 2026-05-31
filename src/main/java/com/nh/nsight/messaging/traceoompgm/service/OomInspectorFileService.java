package com.nh.nsight.messaging.traceoompgm.service;

import com.nh.nsight.messaging.traceoompgm.config.OomInspectorProperties;
import com.nh.nsight.messaging.traceoompgm.model.OomFileContentView;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class OomInspectorFileService {

    private static final int MAX_BYTES = 512 * 1024;

    private final OomInspectorProperties properties;

    public OomInspectorFileService(OomInspectorProperties properties) {
        this.properties = properties;
    }

    public OomFileContentView readContent(
            String relativePath,
            String sourceRoot,
            String mapperRoot,
            String configPath,
            int highlightLine
    ) throws IOException {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("파일 경로가 비어 있습니다.");
        }
        if (relativePath.contains("..")) {
            throw new IllegalArgumentException("허용되지 않는 경로입니다.");
        }

        Path src = resolveRoot(sourceRoot, properties.getDefaultSourceRoot());
        Path mapper = resolveRoot(mapperRoot, properties.getDefaultMapperRoot());
        Path config = resolveConfig(configPath, properties.getDefaultConfigPath());

        Path file = locateFile(relativePath, src, mapper, config);
        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + relativePath);
        }

        byte[] bytes = Files.readAllBytes(file);
        boolean truncated = bytes.length > MAX_BYTES;
        if (truncated) {
            bytes = java.util.Arrays.copyOf(bytes, MAX_BYTES);
        }

        String content = new String(bytes, StandardCharsets.UTF_8);
        int totalLines = content.isEmpty() ? 0 : content.split("\n", -1).length;

        return new OomFileContentView(
                relativePath.replace('\\', '/'),
                file.toString().replace('\\', '/'),
                totalLines,
                Math.max(highlightLine, 0),
                content,
                truncated,
                StandardCharsets.UTF_8.name()
        );
    }

    private Path resolveRoot(String override, String defaultPath) {
        String path = override != null && !override.isBlank() ? override : defaultPath;
        return Path.of(path).toAbsolutePath().normalize();
    }

    private Path resolveConfig(String override, String defaultPath) {
        String path = override != null && !override.isBlank() ? override : defaultPath;
        return Path.of(path).toAbsolutePath().normalize();
    }

    private Path locateFile(String relativePath, Path sourceRoot, Path mapperRoot, Path configFile) {
        String rel = relativePath.replace('\\', '/');
        List<Path> candidates = new ArrayList<>();
        candidates.add(sourceRoot.resolve(rel));
        if (Files.isDirectory(mapperRoot)) {
            candidates.add(mapperRoot.resolve(rel));
        }
        if (Files.isRegularFile(configFile)) {
            Path configParent = configFile.getParent();
            if (configParent != null) {
                candidates.add(configParent.resolve(rel));
            }
            if (rel.equals(configFile.getFileName().toString())) {
                candidates.add(configFile);
            }
        }

        for (Path candidate : candidates) {
            Path normalized = candidate.normalize().toAbsolutePath();
            if (!Files.isRegularFile(normalized)) {
                continue;
            }
            if (isUnderScanRoots(normalized, sourceRoot, mapperRoot, configFile)) {
                return normalized;
            }
        }
        throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + relativePath);
    }

    private boolean isUnderScanRoots(Path file, Path sourceRoot, Path mapperRoot, Path configFile) {
        if (isChildOf(file, sourceRoot)) {
            return true;
        }
        if (Files.isDirectory(mapperRoot) && isChildOf(file, mapperRoot)) {
            return true;
        }
        if (Files.isRegularFile(configFile)) {
            if (file.equals(configFile.normalize().toAbsolutePath())) {
                return true;
            }
            Path parent = configFile.getParent();
            return parent != null && isChildOf(file, parent.normalize().toAbsolutePath());
        }
        return false;
    }

    private boolean isChildOf(Path file, Path root) {
        Path rootAbs = root.toAbsolutePath().normalize();
        Path fileAbs = file.toAbsolutePath().normalize();
        return fileAbs.startsWith(rootAbs);
    }
}
