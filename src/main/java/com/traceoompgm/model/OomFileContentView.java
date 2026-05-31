package com.traceoompgm.model;

public record OomFileContentView(
        String relativePath,
        String resolvedPath,
        int totalLines,
        int highlightLine,
        String content,
        boolean truncated,
        String encoding
) {
}
