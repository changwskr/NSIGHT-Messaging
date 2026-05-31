package com.nh.nsight.messaging.traceoompgm.collector;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Component
public class SourceFileCollector {

    public record ScannedTextFile(String relativePath, String content, FileKind kind) {
    }

    public enum FileKind {
        JAVA, MAPPER_XML, YAML, PROPERTIES, OTHER
    }

    public List<ScannedTextFile> collect(Path sourceRoot, Path mapperRoot, Path configPath) throws IOException {
        List<ScannedTextFile> files = new ArrayList<>();
        if (Files.isDirectory(sourceRoot)) {
            collectTree(sourceRoot, sourceRoot, FileKind.JAVA, files, ".java");
        }
        if (Files.isDirectory(mapperRoot)) {
            collectTree(mapperRoot, mapperRoot, FileKind.MAPPER_XML, files, ".xml");
        }
        if (configPath != null && Files.isRegularFile(configPath)) {
            files.add(readFile(configPath.getParent() != null ? configPath.getParent() : configPath, configPath));
        }
        return files;
    }

    private void collectTree(Path root, Path dir, FileKind kind, List<ScannedTextFile> out, String ext)
            throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase(Locale.ROOT).endsWith(ext))
                    .forEach(p -> {
                        try {
                            out.add(readFile(root, p));
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    private ScannedTextFile readFile(Path root, Path file) throws IOException {
        String content = Files.readString(file);
        String rel = root.relativize(file).toString().replace('\\', '/');
        FileKind kind = classify(rel, content);
        return new ScannedTextFile(rel, content, kind);
    }

    private FileKind classify(String path, String content) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".java")) {
            return FileKind.JAVA;
        }
        if (lower.endsWith(".xml") && (lower.contains("mapper") || content.contains("<mapper"))) {
            return FileKind.MAPPER_XML;
        }
        if (lower.endsWith(".yml") || lower.endsWith(".yaml")) {
            return FileKind.YAML;
        }
        if (lower.endsWith(".properties")) {
            return FileKind.PROPERTIES;
        }
        return FileKind.OTHER;
    }
}
