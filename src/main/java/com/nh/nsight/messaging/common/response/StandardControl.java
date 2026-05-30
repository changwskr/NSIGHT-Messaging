package com.nh.nsight.messaging.common.response;

public record StandardControl(
        int timeout,
        String retryYn,
        int retryCount,
        Integer pageNo,
        Integer pageSize,
        Long totalCount
) {
    public static StandardControl online() {
        return new StandardControl(5000, "N", 0, null, null, null);
    }

    public static StandardControl page(Integer pageNo, Integer pageSize, Long totalCount) {
        return new StandardControl(5000, "N", 0, pageNo, pageSize, totalCount);
    }
}
