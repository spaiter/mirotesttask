package com.miro.api.widgets.testtask.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application configuration class. Allow to add external beans and configure app.
 */
@Component
public class AppConfig implements WebMvcConfigurer {

    private Config config;

    private RateLimitConfig rateLimitConfig;

    public AppConfig(Config config, RateLimitConfig rateLimitConfig) {
        this.config = config;
        this.rateLimitConfig = rateLimitConfig;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public RateLimitConfig getRateLimitConfig() {
        return rateLimitConfig;
    }

    public void setRateLimitConfig(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }
}