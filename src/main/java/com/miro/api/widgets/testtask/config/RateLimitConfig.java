package com.miro.api.widgets.testtask.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "application.ratelimit")
@RefreshScope
public class RateLimitConfig {
    private Integer global;
    private List<EndpointRateLimit> endpoints;

    public Integer getGlobal() {
        return global;
    }

    public void setGlobal(Integer global) {
        this.global = global;
    }

    public List<EndpointRateLimit> getEndpoints() {
        return endpoints != null ? endpoints : Collections.emptyList();
    }

    public void setEndpoints(List<EndpointRateLimit> endpoints) {
        this.endpoints = endpoints;
    }
}
