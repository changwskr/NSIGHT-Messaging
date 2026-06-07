package com.nh.nsight.messaging.zpilotfwk.tcf.routing;

import java.util.Map;

import com.nh.nsight.messaging.zpilotfwk.tcf.EPlatonEvent;

/** {@code eventNo} / {@code operationName}에서 BTF serviceId를 추출한다. */
public final class SpRouteKeyResolver {

    private static final Map<String, String> LEGACY_EVENT_NO = Map.of(
            "71001", "SP_COMMON",
            "71002", "SP_ORDER",
            "72001", "SP_COMRC");

    private SpRouteKeyResolver() {
    }

    public static String serviceId(EPlatonEvent event) {
        if (event == null) {
            throw new SpServiceRoutingException("ETCF0001", "event is null");
        }

        String fromEventNo = fromEventNo(event);
        if (fromEventNo != null) {
            return fromEventNo;
        }

        String fromLegacyEventNo = fromLegacyEventNo(event);
        if (fromLegacyEventNo != null) {
            return fromLegacyEventNo;
        }

        String operationName = readOperationName(event);
        String fromOperation = fromOperationName(operationName);
        if (fromOperation != null) {
            return fromOperation;
        }

        String fromWebOperation = fromLegacyWebOperation(operationName);
        if (fromWebOperation != null) {
            return fromWebOperation;
        }

        throw new SpServiceRoutingException("ETCF0002",
                "Cannot resolve serviceId eventNo="
                        + (event.getCommon() != null ? event.getCommon().getEventNo() : null));
    }

    public static String transactionCode(EPlatonEvent event, String serviceId) {
        if (event == null || event.getCommon() == null || serviceId == null) {
            return "";
        }
        String eventNo = event.getCommon().getEventNo();
        if (eventNo == null || !eventNo.startsWith(serviceId)) {
            return "";
        }
        return eventNo.substring(serviceId.length());
    }

    private static String fromEventNo(EPlatonEvent event) {
        if (event.getCommon() == null) {
            return null;
        }
        String eventNo = trim(event.getCommon().getEventNo());
        if (eventNo == null || eventNo.isEmpty() || "*".equals(eventNo)) {
            return null;
        }
        if (!eventNo.startsWith("SP_")) {
            return null;
        }
        String serviceId = stripTrailingDigits(eventNo);
        if (serviceId.isEmpty() || serviceId.equals(eventNo)) {
            return null;
        }
        return serviceId;
    }

    private static String fromLegacyEventNo(EPlatonEvent event) {
        if (event.getCommon() == null) {
            return null;
        }
        String eventNo = trim(event.getCommon().getEventNo());
        if (eventNo == null || eventNo.isEmpty() || "*".equals(eventNo)) {
            return null;
        }
        return LEGACY_EVENT_NO.get(eventNo);
    }

    private static String fromLegacyWebOperation(String operationName) {
        if (operationName == null || !operationName.startsWith("WEB.sp-")) {
            return null;
        }
        String rest = operationName.substring("WEB.sp-".length());
        int dot = rest.indexOf('.');
        String slug = dot > 0 ? rest.substring(0, dot) : rest;
        if (slug.isBlank()) {
            return null;
        }
        return "SP_" + slug.replace('-', '_').toUpperCase();
    }

    private static String fromOperationName(String operationName) {
        if (operationName == null) {
            return null;
        }
        // AC_SP_ORDER.execute → SP_ORDER
        if (!operationName.startsWith("AC_SP_")) {
            return null;
        }
        int dot = operationName.indexOf('.', 3);
        String segment = dot > 0 ? operationName.substring(3, dot) : operationName.substring(3);
        if (segment.isBlank()) {
            return null;
        }
        return segment;
    }

    private static String readOperationName(EPlatonEvent event) {
        if (event.getCommon() != null && !isBlank(event.getCommon().getOperationName())) {
            return trim(event.getCommon().getOperationName());
        }
        if (event.getTPSVCINFODTO() != null && !isBlank(event.getTPSVCINFODTO().getOperation_name())) {
            return trim(event.getTPSVCINFODTO().getOperation_name());
        }
        return null;
    }

    private static String stripTrailingDigits(String value) {
        int end = value.length() - 1;
        while (end >= 0 && Character.isDigit(value.charAt(end))) {
            end--;
        }
        return value.substring(0, end + 1);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank() || "*".equals(value.trim());
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
