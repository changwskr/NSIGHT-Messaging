package com.nh.nsight.messaging.traceenvironment.service;

import com.nh.nsight.messaging.traceenvironment.model.ParsedConfigEntry;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigParserServiceTest {

    private final ConfigParserService parser = new ConfigParserService();

    @Test
    void parseYaml_multiDocument_mergesProfiles() throws Exception {
        String yaml = """
                spring:
                  application:
                    name: demo-app
                server:
                  port: 8080
                ---
                spring:
                  config:
                    activate:
                      on-profile: prd
                server:
                  port: 9090
                """;

        List<ParsedConfigEntry> entries = parser.parse("application.yml", yaml.getBytes(StandardCharsets.UTF_8));
        Map<String, String> byKey = entries.stream()
                .collect(Collectors.toMap(ParsedConfigEntry::normalizedKey, ParsedConfigEntry::configValue, (a, b) -> b));

        assertThat(byKey).containsEntry("spring.application.name", "demo-app");
        assertThat(byKey).containsEntry("server.port", "9090");
    }
}
