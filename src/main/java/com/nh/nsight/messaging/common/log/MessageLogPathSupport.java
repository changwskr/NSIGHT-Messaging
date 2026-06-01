package com.nh.nsight.messaging.common.log;

public final class MessageLogPathSupport {

    private MessageLogPathSupport() {
    }

    public static boolean shouldSkipLogging(String uri) {
        if (uri == null || uri.isBlank()) {
            return true;
        }
        if (uri.startsWith("/home/login")
                || uri.startsWith("/tracedump")
                || uri.startsWith("/api/v1/trace-dump")
                || uri.startsWith("/transactionmgr")
                || uri.startsWith("/api/v1/transaction-logs")
                || uri.startsWith("/api/v1/message-logs")
                || uri.startsWith("/xpilottransactionmgr")
                || uri.startsWith("/api/xpilottransactionmgr")
                || uri.contains("/h2-console")
                || uri.startsWith("/actuator")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.equals("/favicon.ico")) {
            return true;
        }
        return uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".ico");
    }

    public static String safePathSegment(String value) {
        if (value == null || value.isBlank()) {
            return "UNKNOWN";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static String safeUriSegment(String uri) {
        String segment = uri == null ? "root" : uri.replaceFirst("^/", "").replace('/', '_');
        if (segment.length() > 80) {
            segment = segment.substring(0, 80);
        }
        return safePathSegment(segment);
    }
}
