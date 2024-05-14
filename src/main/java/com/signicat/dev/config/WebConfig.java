package com.signicat.dev.config;

import com.signicat.dev.interceptor.RateLimitingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${archive.service.rate.limit}")
    private long maxRequest;

    @Value("${archive.service.rate.limit.duration.mins}")
    private long rateLimitDurationInMinutes;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor(maxRequest, rateLimitDurationInMinutes));
    }
}
