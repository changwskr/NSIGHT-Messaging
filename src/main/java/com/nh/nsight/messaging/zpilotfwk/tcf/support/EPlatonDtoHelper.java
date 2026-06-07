package com.nh.nsight.messaging.zpilotfwk.tcf.support;

/**
 * {@link com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent}의 {@link IDTO} 타입 변환 유틸.
 */
public final class EPlatonDtoHelper {

    private EPlatonDtoHelper() {
    }

    public static <T extends IDTO> T cast(IDTO dto, Class<T> type) {
        if (dto == null || type == null) {
            return null;
        }
        if (type.isInstance(dto)) {
            return type.cast(dto);
        }
        return null;
    }
}
