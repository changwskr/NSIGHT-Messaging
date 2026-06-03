package com.nh.nsight.messaging.xpilottpcclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nh.nsight.messaging.common.response.StandardResponse;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageCreateCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageSearchCDTO;
import com.nh.nsight.messaging.xpilotmessaging.ac.messageac.dto.MessageUpdateCDTO;
import com.nh.nsight.messaging.xpilottpcclient.dto.MessageSearchResult;
import com.nh.nsight.messaging.xpilottpcclient.util.TpcClientException;
import com.nh.nsight.messaging.xpilottpcclient.util.TpcMessageMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;

/**
 * TPC(Third Party Client) — HTTP JSON으로 xpilotmessaging API 호출.
 * <p>기본 URL: {@code http://localhost:8080} · 경로: {@code /api/xpilotmessaging/messages}</p>
 */
public class TpcUtil {

    private static final String MESSAGES_PATH = "/api/xpilotmessaging/messages";
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TpcUtil() {
        this(DEFAULT_BASE_URL);
    }

    public TpcUtil(String baseUrl) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .build();
        this.objectMapper = createObjectMapper();
    }

    /** 테스트·주입용 */
    TpcUtil(String baseUrl, HttpClient httpClient, ObjectMapper objectMapper) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /** POST — 메시지 등록 (xpilotmessaging MessageCreateCDTO) */
    public MessageCDTO createMessage(MessageCreateCDTO request) {
        HttpRequest httpRequest = jsonRequest(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + MESSAGES_PATH))
                .timeout(REQUEST_TIMEOUT)
                .POST(HttpRequest.BodyPublishers.ofString(toJson(request))));
        return unwrap(exchange(httpRequest, new TypeReference<>() {
        }));
    }

    /** POST — MessageCDTO 기준 등록 */
    public MessageCDTO createMessage(MessageCDTO request) {
        return createMessage(TpcMessageMapper.toCreateRequest(request));
    }

    /** GET — 메시지 단건 */
    public MessageCDTO getMessage(long messageId) {
        StandardResponse<MessageCDTO> envelope = exchange(
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + MESSAGES_PATH + "/" + messageId))
                        .timeout(REQUEST_TIMEOUT)
                        .header("Accept", "application/json")
                        .GET()
                        .build(),
                new TypeReference<>() {
                });
        return unwrap(envelope);
    }

    /** GET — 메시지 목록(페이징) */
    public MessageSearchResult searchMessages(MessageSearchCDTO criteria) {
        MessageSearchCDTO c = criteria != null ? criteria : new MessageSearchCDTO();
        String query = buildSearchQuery(c);
        String path = query.isEmpty() ? MESSAGES_PATH : MESSAGES_PATH + "?" + query;

        StandardResponse<List<MessageCDTO>> envelope = exchange(
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + path))
                        .timeout(REQUEST_TIMEOUT)
                        .header("Accept", "application/json")
                        .GET()
                        .build(),
                new TypeReference<>() {
                });
        List<MessageCDTO> list = unwrap(envelope);
        int pageNo = envelope.control() != null && envelope.control().pageNo() != null
                ? envelope.control().pageNo() : safeInt(c.getPageNo(), 1);
        int pageSize = envelope.control() != null && envelope.control().pageSize() != null
                ? envelope.control().pageSize() : safeInt(c.getPageSize(), 3);
        long total = envelope.control() != null && envelope.control().totalCount() != null
                ? envelope.control().totalCount() : list.size();
        return new MessageSearchResult(list, pageNo, pageSize, total);
    }

    /** PUT — 메시지 수정 (xpilotmessaging MessageUpdateCDTO) */
    public MessageCDTO updateMessage(long messageId, MessageUpdateCDTO request) {
        HttpRequest httpRequest = jsonRequest(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + MESSAGES_PATH + "/" + messageId))
                .timeout(REQUEST_TIMEOUT)
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(request))));
        return unwrap(exchange(httpRequest, new TypeReference<>() {
        }));
    }

    /** PUT — MessageCDTO 기준 수정 */
    public MessageCDTO updateMessage(long messageId, MessageCDTO request) {
        return updateMessage(messageId, TpcMessageMapper.toUpdateRequest(request));
    }

    /** DELETE — 메시지 삭제 */
    public void deleteMessage(long messageId) {
        StandardResponse<Void> envelope = exchange(
                HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + MESSAGES_PATH + "/" + messageId))
                        .timeout(REQUEST_TIMEOUT)
                        .header("Accept", "application/json")
                        .DELETE()
                        .build(),
                new TypeReference<>() {
                });
        assertSuccess(envelope);
    }

    /** JSON 헤더 적용 후 {@link HttpRequest} 생성 (Builder를 exchange에 넘기지 않도록). */
    private static HttpRequest jsonRequest(HttpRequest.Builder builder) {
        return builder
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json")
                .build();
    }

    private <T> StandardResponse<T> exchange(HttpRequest request, TypeReference<StandardResponse<T>> typeRef) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            String body = response.body() != null ? response.body() : "";

            if (status < 200 || status >= 300) {
                throw new TpcClientException(
                        "HTTP " + status + " " + request.method() + " " + request.uri(),
                        status,
                        body);
            }
            if (body.isBlank()) {
                throw new TpcClientException("Empty response body: " + request.uri());
            }
            return objectMapper.readValue(body, typeRef);
        } catch (TpcClientException e) {
            throw e;
        } catch (Exception e) {
            throw new TpcClientException("xpilotmessaging HTTP call failed: " + request.uri(), e);
        }
    }

    private <T> T unwrap(StandardResponse<T> envelope) {
        assertSuccess(envelope);
        if (envelope.body() == null) {
            throw new TpcClientException("Response body is null");
        }
        T data = envelope.body().response();
        if (data == null) {
            throw new TpcClientException("Response data is null (resultCode=SUCCESS but empty payload)");
        }
        return data;
    }

    private void assertSuccess(StandardResponse<?> envelope) {
        if (envelope == null || envelope.error() == null) {
            throw new TpcClientException("Invalid StandardResponse envelope");
        }
        if (!"SUCCESS".equals(envelope.error().resultCode())) {
            throw new TpcClientException(
                    "API error: " + envelope.error().resultCode()
                            + " / " + envelope.error().errorCode()
                            + " — " + envelope.error().errorMessage()
                            + (envelope.error().errorDetail() != null && !envelope.error().errorDetail().isBlank()
                            ? " (" + envelope.error().errorDetail() + ")" : ""));
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new TpcClientException("JSON serialize failed", e);
        }
    }

    private static String buildSearchQuery(MessageSearchCDTO c) {
        StringJoiner joiner = new StringJoiner("&");
        appendParam(joiner, "messageType", c.getMessageType());
        appendParam(joiner, "channelCode", c.getChannelCode());
        appendParam(joiner, "useYn", c.getUseYn());
        if (c.getPageNo() != null) {
            appendParam(joiner, "pageNo", String.valueOf(c.getPageNo()));
        }
        if (c.getPageSize() != null) {
            appendParam(joiner, "pageSize", String.valueOf(c.getPageSize()));
        }
        return joiner.toString();
    }

    private static void appendParam(StringJoiner joiner, String name, String value) {
        if (value != null && !value.isBlank()) {
            joiner.add(encode(name) + "=" + encode(value));
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static int safeInt(Integer value, int defaultValue) {
        return value != null && value > 0 ? value : defaultValue;
    }

    private static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return DEFAULT_BASE_URL;
        }
        String trimmed = baseUrl.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    /**
     * xpilotmessaging HTTP 클라이언트 실행 샘플.
     * <p>사용: 앱 기동 후 {@code java ... TpcUtil [baseUrl] [list|get|create] ...}</p>
     * <ul>
     *   <li>인자 없음 — 목록 조회(1페이지, 10건, useYn=Y) + 첫 건 상세</li>
     *   <li>{@code list} — 목록만</li>
     *   <li>{@code get {messageId}} — 단건</li>
     *   <li>{@code create} — TPC 샘플 메시지 1건 등록</li>
     * </ul>
     */
    public static void main(String[] args) {
        String baseUrl = DEFAULT_BASE_URL;
        String command = "demo";
        int argIdx = 0;
        if (args.length > 0 && looksLikeUrl(args[0])) {
            baseUrl = args[0];
            argIdx = 1;
        }
        if (args.length > argIdx) {
            command = args[argIdx].toLowerCase();
        }

        TpcUtil client = new TpcUtil(baseUrl);
        System.out.println("=== TpcUtil · xpilotmessaging ===");
        System.out.println("baseUrl: " + client.getBaseUrl());
        System.out.println("command: " + command);

        try {
            switch (command) {
                case "list" -> runList(client);
                case "get" -> runGet(client, args, argIdx);
                case "create" -> runCreate(client);
                case "demo" -> {
                    runList(client);
                    MessageSearchCDTO criteria = new MessageSearchCDTO();
                    criteria.setPageNo(1);
                    criteria.setPageSize(10);
                    criteria.setUseYn("Y");
                    MessageSearchResult result = client.searchMessages(criteria);
                    if (!result.messages().isEmpty()) {
                        Long id = result.messages().get(0).getMessageId();
                        if (id != null) {
                            System.out.println("--- 단건 조회 (첫 행) ---");
                            printDetail(client.getMessage(id));
                        }
                    }
                }
                default -> {
                    System.err.println("Unknown command: " + command);
                    printUsage();
                    System.exit(2);
                }
            }
            System.out.println("DONE");
        } catch (TpcClientException e) {
            System.err.println("FAIL: " + e.getMessage());
            if (e.getHttpStatus() > 0) {
                System.err.println("HTTP status: " + e.getHttpStatus());
            }
            System.err.println("※ Spring Boot 앱이 baseUrl에서 기동 중인지 확인하세요.");
            System.exit(1);
        }
    }

    private static void runList(TpcUtil client) {
        MessageSearchCDTO criteria = new MessageSearchCDTO();
        criteria.setPageNo(1);
        criteria.setPageSize(10);
        criteria.setUseYn("Y");
        MessageSearchResult result = client.searchMessages(criteria);
        System.out.println("목록 page=" + result.pageNo() + " size=" + result.pageSize()
                + " total=" + result.totalCount());
        for (MessageCDTO m : result.messages()) {
            System.out.printf("  [%s] %s · %s · %s · useYn=%s%n",
                    m.getMessageId(), m.getMessageCode(), m.getMessageName(), m.getMessageType(), m.getUseYn());
        }
    }

    private static void runGet(TpcUtil client, String[] args, int commandIdx) {
        if (args.length <= commandIdx + 1) {
            System.err.println("Usage: TpcUtil [baseUrl] get <messageId>");
            System.exit(2);
        }
        long id = Long.parseLong(args[commandIdx + 1]);
        printDetail(client.getMessage(id));
    }

    private static void runCreate(TpcUtil client) {
        String code = "TPC_MAIN_" + System.currentTimeMillis();
        MessageCreateCDTO req = new MessageCreateCDTO();
        req.setMessageCode(code);
        req.setMessageName("TPC main() 샘플");
        req.setMessageType("INFO");
        req.setChannelCode("API");
        req.setLocale("ko_KR");
        req.setMessageContent("TpcUtil.main()에서 등록한 테스트 메시지");
        req.setUseYn("Y");
        MessageCDTO created = client.createMessage(req);
        System.out.println("등록 완료 id=" + created.getMessageId() + " code=" + created.getMessageCode());
    }

    private static void printDetail(MessageCDTO m) {
        System.out.println("  id=" + m.getMessageId());
        System.out.println("  code=" + m.getMessageCode());
        System.out.println("  name=" + m.getMessageName());
        System.out.println("  content=" + m.getMessageContent());
        System.out.println("  activeNow=" + m.getActiveNow());
    }

    private static boolean looksLikeUrl(String s) {
        return s.startsWith("http://") || s.startsWith("https://");
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  TpcUtil [http://localhost:8080]           — 목록 + 첫 건 상세");
        System.out.println("  TpcUtil [baseUrl] list                    — 목록");
        System.out.println("  TpcUtil [baseUrl] get <messageId>         — 단건");
        System.out.println("  TpcUtil [baseUrl] create                  — 샘플 등록");
    }
}
