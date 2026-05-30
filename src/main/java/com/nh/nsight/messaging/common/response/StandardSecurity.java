package com.nh.nsight.messaging.common.response;

public record StandardSecurity(
        String maskingLevel,
        String dataGrade,
        String accessPurpose,
        String auditRequiredYn
) {
    public static StandardSecurity general() {
        return new StandardSecurity("NONE", "INTERNAL", "MESSAGE_MANAGEMENT", "Y");
    }
}
