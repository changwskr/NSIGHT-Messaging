package com.nh.nsight.messaging.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MessageEnvelopeProperties.class)
public class MessageEnvelopeConfig {
}
