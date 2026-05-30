package com.nh.nsight.messaging.common.log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nh.nsight.messaging.common.context.RequestContext;
import com.nh.nsight.messaging.common.response.StandardControl;
import com.nh.nsight.messaging.common.response.StandardHeader;
import com.nh.nsight.messaging.common.response.StandardSecurity;
import com.nh.nsight.messaging.config.MessageEnvelopeProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.nh.nsight.messaging.transactionmgr.thing.TransactionLog;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class MessageEnvelopeFileService {

    private static final Logger log = LoggerFactory.getLogger(MessageEnvelopeFileService.class);
    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter FILE_STAMP = DateTimeFormatter.ofPattern("HHmmssSSS");

    private final MessageEnvelopeProperties properties;
    private final ObjectMapper objectMapper;

    public MessageEnvelopeFileService(MessageEnvelopeProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper.copy()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void persistExchange(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        if (!properties.isEnabled()) {
            return;
        }
        String uri = request.getRequestURI();
        if (MessageLogPathSupport.shouldSkipLogging(uri)) {
            return;
        }

        try {
            String guid = resolveGuid();
            LocalDateTime now = LocalDateTime.now();
            String baseName = FILE_STAMP.format(now)
                    + "_" + request.getMethod()
                    + "_" + MessageLogPathSupport.safeUriSegment(uri);

            Path directory = Path.of(properties.getStoragePath())
                    .resolve(DATE_DIR.format(now))
                    .resolve(MessageLogPathSupport.safePathSegment(guid));
            Files.createDirectories(directory);

            Path reqFile = directory.resolve(baseName + "-REQ.json");
            Path resFile = directory.resolve(baseName + "-RES.json");

            byte[] requestDoc = buildRequestDocument(request, uri);
            byte[] responseDoc = buildResponseDocument(request, response);

            writeFile(reqFile, requestDoc);
            writeFile(resFile, responseDoc);

            log.debug("[MSG-FILE] guid={} req={} res={}", guid, reqFile, resFile);
        } catch (Exception ex) {
            log.warn("[MSG-FILE-SKIP] uri={} error={}", uri, ex.getMessage());
        }
    }

    private String resolveGuid() {
        RequestContext.Context context = RequestContext.get();
        if (context != null && StringUtils.hasText(context.guid())) {
            return context.guid();
        }
        return "NO-GUID";
    }

    private byte[] buildRequestDocument(ContentCachingRequestWrapper request, String uri) throws Exception {
        RequestEnvelopeResolver.ResolvedService resolved = RequestEnvelopeResolver.resolve(request.getMethod(), uri);
        StandardHeader header = StandardHeader.request(resolved.transactionId(), resolved.serviceId());
        StandardControl control = StandardControl.online();
        StandardSecurity security = StandardSecurity.general();

        ObjectNode root = objectMapper.createObjectNode();
        root.put("type", "REQUEST");
        root.put("httpMethod", request.getMethod());
        root.put("requestUri", uri);
        if (StringUtils.hasText(request.getQueryString())) {
            root.put("queryString", request.getQueryString());
        }
        root.put("contentType", request.getContentType() == null ? "" : request.getContentType());
        root.set("header", objectMapper.valueToTree(header));
        root.set("control", objectMapper.valueToTree(control));
        root.set("security", objectMapper.valueToTree(security));
        appendHttpHeaders(root, request);
        appendRequestBody(root, request);
        return objectMapper.writeValueAsBytes(root);
    }

    private void appendHttpHeaders(ObjectNode root, HttpServletRequest request) {
        ObjectNode httpHeaders = objectMapper.createObjectNode();
        addHeader(httpHeaders, request, "X-GUID");
        addHeader(httpHeaders, request, "X-TRACE-ID");
        addHeader(httpHeaders, request, "X-USER-ID");
        addHeader(httpHeaders, request, "X-BRANCH-ID");
        addHeader(httpHeaders, request, "X-CENTER-ID");
        addHeader(httpHeaders, request, "X-TERMINAL-ID");
        addHeader(httpHeaders, request, "Content-Type");
        root.set("httpHeaders", httpHeaders);
    }

    private void appendRequestBody(ObjectNode root, ContentCachingRequestWrapper request) throws Exception {
        byte[] body = truncate(request.getContentAsByteArray());
        if (body.length == 0) {
            root.putNull("body");
            return;
        }
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("multipart")) {
            root.put("body", "(multipart/form-data, " + body.length + " bytes omitted)");
            return;
        }
        root.set("body", parseBodyNode(body, resolveCharset(request.getCharacterEncoding())));
    }

    private byte[] buildResponseDocument(HttpServletRequest request, ContentCachingResponseWrapper response)
            throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("type", "RESPONSE");
        root.put("httpMethod", request.getMethod());
        root.put("requestUri", request.getRequestURI());
        root.put("status", response.getStatus());
        root.put("contentType", response.getContentType() == null ? "" : response.getContentType());

        byte[] body = truncate(response.getContentAsByteArray());
        if (body.length == 0) {
            root.putNull("body");
            return objectMapper.writeValueAsBytes(root);
        }

        JsonNode bodyNode = parseBodyNode(body, resolveCharset(response.getCharacterEncoding()));
        if (isStandardResponseEnvelope(bodyNode)) {
            root.set("header", bodyNode.get("header"));
            root.set("control", bodyNode.get("control"));
            root.set("security", bodyNode.get("security"));
            root.set("error", bodyNode.get("error"));
            root.set("body", bodyNode.get("body"));
        } else {
            root.set("body", bodyNode);
        }
        return objectMapper.writeValueAsBytes(root);
    }

    private static boolean isStandardResponseEnvelope(JsonNode bodyNode) {
        return bodyNode != null
                && bodyNode.isObject()
                && bodyNode.has("header")
                && bodyNode.has("control")
                && bodyNode.has("security")
                && bodyNode.has("error");
    }

    private JsonNode parseBodyNode(byte[] body, Charset charset) throws Exception {
        String text = new String(body, charset);
        if (!StringUtils.hasText(text)) {
            return objectMapper.nullNode();
        }
        try {
            return objectMapper.readTree(text);
        } catch (Exception ignored) {
            return objectMapper.getNodeFactory().textNode(text);
        }
    }

    private byte[] truncate(byte[] body) {
        int max = properties.getMaxBodyBytes();
        if (body.length <= max) {
            return body;
        }
        byte[] truncated = new byte[max];
        System.arraycopy(body, 0, truncated, 0, max);
        return truncated;
    }

    private void writeFile(Path path, byte[] content) throws Exception {
        Files.write(path, content);
    }

    private static void addHeader(ObjectNode headers, HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtils.hasText(value)) {
            headers.put(name, value);
        }
    }

    private static Charset resolveCharset(String encoding) {
        if (!StringUtils.hasText(encoding)) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception ex) {
            return StandardCharsets.UTF_8;
        }
    }

    public EnvelopeFileDeleteResult deleteForTransactionLog(TransactionLog txLog) {
        if (!properties.isEnabled() || txLog == null) {
            return EnvelopeFileDeleteResult.empty();
        }
        if (txLog.getCreatedAt() == null || !StringUtils.hasText(txLog.getGuid())) {
            return EnvelopeFileDeleteResult.empty();
        }

        Path directory = resolveGuidDirectory(txLog);
        if (!Files.isDirectory(directory)) {
            return EnvelopeFileDeleteResult.empty();
        }

        List<String> deletedPaths = new ArrayList<>();
        String matchKey = "_" + txLog.getHttpMethod() + "_" + MessageLogPathSupport.safeUriSegment(txLog.getRequestUri());
        String timePrefix = FILE_STAMP.format(txLog.getCreatedAt());

        try (Stream<Path> files = Files.list(directory)) {
            List<Path> candidates = files
                    .filter(Files::isRegularFile)
                    .filter(path -> isEnvelopeFile(path.getFileName().toString()))
                    .toList();

            for (Path path : candidates) {
                String name = path.getFileName().toString();
                if (name.contains(matchKey) || name.startsWith(timePrefix)) {
                    Files.deleteIfExists(path);
                    deletedPaths.add(path.toAbsolutePath().normalize().toString());
                }
            }
        } catch (IOException ex) {
            log.warn("[MSG-FILE-DELETE-SKIP] guid={} error={}", txLog.getGuid(), ex.getMessage());
            return new EnvelopeFileDeleteResult(deletedPaths.size(), deletedPaths);
        }

        try {
            cleanupEmptyDirectories(directory);
        } catch (IOException ex) {
            log.warn("[MSG-FILE-DELETE-DIR-SKIP] path={} error={}", directory, ex.getMessage());
        }

        log.info("[MSG-FILE-DELETE] guid={} files={}", txLog.getGuid(), deletedPaths.size());
        return new EnvelopeFileDeleteResult(deletedPaths.size(), deletedPaths);
    }

    private Path resolveGuidDirectory(TransactionLog txLog) {
        String dateDir = DATE_DIR.format(txLog.getCreatedAt());
        return Path.of(properties.getStoragePath())
                .resolve(dateDir)
                .resolve(MessageLogPathSupport.safePathSegment(txLog.getGuid()));
    }

    private static boolean isEnvelopeFile(String filename) {
        return filename.endsWith("-REQ.json") || filename.endsWith("-RES.json");
    }

    private static void cleanupEmptyDirectories(Path guidDirectory) throws IOException {
        if (!Files.isDirectory(guidDirectory) || !isDirectoryEmpty(guidDirectory)) {
            return;
        }
        Files.delete(guidDirectory);
        Path dateDirectory = guidDirectory.getParent();
        if (dateDirectory != null && Files.isDirectory(dateDirectory) && isDirectoryEmpty(dateDirectory)) {
            Files.delete(dateDirectory);
        }
    }

    private static boolean isDirectoryEmpty(Path directory) throws IOException {
        try (Stream<Path> entries = Files.list(directory)) {
            return entries.findAny().isEmpty();
        }
    }

    public Map<String, String> storageInfo() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("storagePath", Path.of(properties.getStoragePath()).toAbsolutePath().normalize().toString());
        info.put("enabled", String.valueOf(properties.isEnabled()));
        info.put("pathPattern", "{storagePath}/{yyyyMMdd}/{guid}/{HHmmssSSS}_{METHOD}_{uri}-REQ.json");
        info.put("pathPatternResponse", "{storagePath}/{yyyyMMdd}/{guid}/{HHmmssSSS}_{METHOD}_{uri}-RES.json");
        info.put("requestEnvelope", "header, control, security, body, httpHeaders");
        info.put("responseEnvelope", "header, control, security, error, body (API JSON인 경우 상위 분리)");
        return info;
    }
}
