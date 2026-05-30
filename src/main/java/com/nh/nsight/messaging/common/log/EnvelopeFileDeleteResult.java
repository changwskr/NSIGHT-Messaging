package com.nh.nsight.messaging.common.log;

import java.util.List;

public record EnvelopeFileDeleteResult(int deletedFileCount, List<String> deletedFilePaths) {

    public static EnvelopeFileDeleteResult empty() {
        return new EnvelopeFileDeleteResult(0, List.of());
    }
}
