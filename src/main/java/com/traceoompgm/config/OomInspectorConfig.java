package com.traceoompgm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OomInspectorProperties.class)
public class OomInspectorConfig {
}
