package com.nh.nsight.messaging.junmun.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nh.nsight.messaging.junmun.ac.junmunac.dto.JunmunBuildRequestCDTO;
import com.nh.nsight.messaging.junmun.dc.junmundc.dto.JunmunDefinitionDDTO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * PH1 레이아웃 + 필드값 → JSON 표준전문 envelope 생성·검증.
 */
public final class JunmunJsonEnvelopeBuilder {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JunmunJsonEnvelopeBuilder() {
    }

    public static String build(JunmunDefinitionDDTO definition, JunmunBuildRequestCDTO request) {
        try {
            JsonNode layout = MAPPER.readTree(definition.layoutJson());
            ObjectNode root = MAPPER.createObjectNode();

            ObjectNode meta = root.putObject("meta");
            meta.put("messageCode", definition.messageCode());
            meta.put("messageName", definition.messageName());
            meta.put("standardVersion", definition.standardVersion());
            meta.put("direction", definition.direction());
            meta.put("documentRef", definition.documentRef());

            ObjectNode header = root.putObject("header");
            Map<String, String> values = request != null && request.getFieldValues() != null
                    ? request.getFieldValues()
                    : Map.of();
            applySections(layout, header, root, values);

            if (request != null && request.getControl() != null) {
                root.set("control", MAPPER.valueToTree(request.getControl()));
            } else {
                root.putObject("control")
                        .put("timeout", 30)
                        .put("retryYn", "N")
                        .put("pageNo", 1)
                        .put("pageSize", 20);
            }
            if (request != null && request.getSecurity() != null) {
                root.set("security", MAPPER.valueToTree(request.getSecurity()));
            } else {
                root.putObject("security")
                        .put("maskingLevel", "GENERAL")
                        .put("dataGrade", "INTERNAL")
                        .put("auditRequiredYn", "Y");
            }
            if (request != null && request.getError() != null) {
                root.set("error", MAPPER.valueToTree(request.getError()));
            } else {
                root.putObject("error")
                        .put("resultCode", "REQ".equals(definition.direction()) ? "SUCCESS" : "SUCCESS")
                        .put("resultMessage", "정상");
            }
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (JunmunBizException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JunmunBizException("JSON 전문 생성 실패: " + ex.getMessage());
        }
    }

    private static void applySections(
            JsonNode layout,
            ObjectNode header,
            ObjectNode root,
            Map<String, String> values) throws Exception {
        JsonNode sections = layout.get("sections");
        if (sections == null || !sections.isArray()) {
            return;
        }
        for (JsonNode section : sections) {
            String sectionId = section.path("sectionId").asText("");
            boolean jsonMode = section.path("jsonMode").asBoolean(false);
            ObjectNode sectionNode = jsonMode ? root.putObject("body") : header.putObject(mapSectionKey(sectionId));
            for (JsonNode field : section.path("fields")) {
                String fieldId = field.path("fieldId").asText();
                String value = values.containsKey(fieldId)
                        ? values.get(fieldId)
                        : field.path("defaultValue").asText("");
                if (field.path("required").asBoolean(false)
                        && (value == null || value.isBlank())) {
                    throw new JunmunBizException("필수 필드 누락: " + fieldId
                            + " (" + field.path("name").asText() + ")");
                }
                sectionNode.put(fieldId, value == null ? "" : value);
            }
        }
    }

    private static String mapSectionKey(String sectionId) {
        return switch (sectionId) {
            case "SYS_HDR" -> "system";
            case "COM_HDR" -> "common";
            case "BIZ_HDR" -> "business";
            default -> sectionId.toLowerCase();
        };
    }

    public static List<String> validate(String layoutJson, String envelopeJson) {
        List<String> errors = new ArrayList<>();
        try {
            JsonNode layout = MAPPER.readTree(layoutJson);
            JsonNode envelope = MAPPER.readTree(envelopeJson);
            JsonNode sections = layout.get("sections");
            if (sections == null || !sections.isArray()) {
                errors.add("레이아웃에 sections가 없습니다.");
                return errors;
            }
            for (JsonNode section : sections) {
                String sectionId = section.path("sectionId").asText();
                boolean jsonMode = section.path("jsonMode").asBoolean(false);
                JsonNode sectionNode = resolveSectionNode(envelope, sectionId, jsonMode);
                if (sectionNode == null || !sectionNode.isObject()) {
                    errors.add("섹션 누락: " + sectionId);
                    continue;
                }
                for (JsonNode field : section.path("fields")) {
                    String fieldId = field.path("fieldId").asText();
                    if (field.path("required").asBoolean(false)) {
                        JsonNode val = sectionNode.get(fieldId);
                        if (val == null || val.asText("").isBlank()) {
                            errors.add("필수 필드 누락: " + fieldId);
                        }
                    }
                    if (field.has("length")) {
                        int maxLen = field.path("length").asInt();
                        JsonNode val = sectionNode.get(fieldId);
                        if (val != null && val.asText().length() > maxLen) {
                            errors.add(fieldId + " 길이 초과 (max " + maxLen + ")");
                        }
                    }
                }
            }
            if (!envelope.has("control")) {
                errors.add("control 섹션 누락");
            }
            if (!envelope.has("security")) {
                errors.add("security 섹션 누락");
            }
            if (!envelope.has("error")) {
                errors.add("error 섹션 누락");
            }
        } catch (Exception ex) {
            errors.add("JSON 파싱 오류: " + ex.getMessage());
        }
        return errors;
    }

    private static JsonNode resolveSectionNode(JsonNode envelope, String sectionId, boolean jsonMode) {
        if (jsonMode) {
            return envelope.get("body");
        }
        JsonNode header = envelope.get("header");
        if (header == null) {
            return null;
        }
        return header.get(mapSectionKey(sectionId));
    }

    public static Map<String, String> extractFieldValues(String envelopeJson) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            JsonNode envelope = MAPPER.readTree(envelopeJson);
            collectFromNode(envelope.path("header").path("system"), result);
            collectFromNode(envelope.path("header").path("common"), result);
            collectFromNode(envelope.path("header").path("business"), result);
            collectFromNode(envelope.path("body"), result);
        } catch (Exception ignored) {
            // empty
        }
        return result;
    }

    private static void collectFromNode(JsonNode node, Map<String, String> target) {
        if (node == null || !node.isObject()) {
            return;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            target.put(entry.getKey(), entry.getValue().asText());
        }
    }
}
