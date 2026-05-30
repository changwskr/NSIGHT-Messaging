package com.nh.nsight.messaging.common.log;

public final class RequestEnvelopeResolver {

    private RequestEnvelopeResolver() {
    }

    public static ResolvedService resolve(String httpMethod, String requestUri) {
        if (requestUri == null) {
            return new ResolvedService("REQ-UNKNOWN-001", "unknown");
        }
        String method = httpMethod == null ? "GET" : httpMethod.toUpperCase();
        String uri = requestUri;

        if (uri.startsWith("/api/v1/messages")) {
            if ("POST".equals(method)) {
                return new ResolvedService("MSG-CREATE-001", "messageCreate");
            }
            if ("PUT".equals(method)) {
                return new ResolvedService("MSG-UPDATE-001", "messageUpdate");
            }
            if ("DELETE".equals(method)) {
                return new ResolvedService("MSG-DELETE-001", "messageDelete");
            }
            if (uri.matches(".*/api/v1/messages/\\d+")) {
                return new ResolvedService("MSG-DETAIL-001", "messageDetail");
            }
            return new ResolvedService("MSG-LIST-001", "messageList");
        }
        if (uri.startsWith("/api/v1/files")) {
            if ("POST".equals(method)) {
                return new ResolvedService("FILE-UPLOAD-001", "fileUpload");
            }
            if ("DELETE".equals(method)) {
                return new ResolvedService("FILE-DELETE-001", "fileDelete");
            }
            if (uri.contains("/download")) {
                return new ResolvedService("FILE-DOWNLOAD-001", "fileDownload");
            }
            if (uri.contains("/storage-location")) {
                return new ResolvedService("FILE-LOC-001", "fileStorageLocation");
            }
            if (uri.matches(".*/api/v1/files/\\d+")) {
                return new ResolvedService("FILE-DETAIL-001", "fileDetail");
            }
            return new ResolvedService("FILE-LIST-001", "fileList");
        }
        if (uri.startsWith("/api/v1/transaction-logs")) {
            if (uri.matches(".*/api/v1/transaction-logs/\\d+")) {
                return new ResolvedService("TX-LOG-DETAIL-001", "transactionLogDetail");
            }
            if ("DELETE".equals(method)) {
                return new ResolvedService("TX-LOG-DELETE-001", "transactionLogDelete");
            }
            return new ResolvedService("TX-LOG-LIST-001", "transactionLogList");
        }
        if (uri.startsWith("/api/v1/message-logs")) {
            return new ResolvedService("MSG-LOG-LOC-001", "messageLogStorageLocation");
        }

        String serviceId = uri.replaceFirst("^/api/v1/", "")
                .replace('/', '_')
                .replaceAll("[^a-zA-Z0-9_]", "");
        if (serviceId.isBlank()) {
            serviceId = MessageLogPathSupport.safeUriSegment(uri);
        }
        return new ResolvedService("REQ-" + method + "-" + serviceId.toUpperCase(), serviceId);
    }

    public record ResolvedService(String transactionId, String serviceId) {
    }
}
