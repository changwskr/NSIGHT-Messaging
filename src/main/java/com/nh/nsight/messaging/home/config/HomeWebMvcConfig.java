package com.nh.nsight.messaging.home.config;

import com.nh.nsight.messaging.home.interceptor.HomeAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class HomeWebMvcConfig implements WebMvcConfigurer {

    private final HomeAuthInterceptor homeAuthInterceptor;

    public HomeWebMvcConfig(HomeAuthInterceptor homeAuthInterceptor) {
        this.homeAuthInterceptor = homeAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(homeAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/home/login",
                        "/home/logout",
                        "/api/**",
                        "/actuator/**",
                        "/h2-console/**",
                        "/css/**",
                        "/js/**",
                        "/favicon.ico",
                        "/error"
                );
    }
}
