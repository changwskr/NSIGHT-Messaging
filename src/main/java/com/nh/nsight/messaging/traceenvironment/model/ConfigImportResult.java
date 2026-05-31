package com.nh.nsight.messaging.traceenvironment.model;

import java.util.List;

public record ConfigImportResult(
        String importId,
        int fileCount,
        int entryCount,
        List<String> fileNames,
        List<ParsedConfigEntry> entries
) {
}
