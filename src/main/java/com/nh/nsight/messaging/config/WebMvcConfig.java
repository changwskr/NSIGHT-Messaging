package com.nh.nsight.messaging.config;

import com.nh.nsight.messaging.common.log.GuidMdcFilter;
import com.nh.nsight.messaging.common.log.MessageEnvelopeCaptureFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebMvcConfig {

    @Bean
    public FilterRegistrationBean<GuidMdcFilter> guidMdcFilterRegistration(GuidMdcFilter filter) {
        FilterRegistrationBean<GuidMdcFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<MessageEnvelopeCaptureFilter> messageEnvelopeCaptureFilterRegistration(
            MessageEnvelopeCaptureFilter filter
    ) {
        FilterRegistrationBean<MessageEnvelopeCaptureFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }
}
