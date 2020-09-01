package com.miro.api.widgets.testtask.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RateLimitConfig implements Converter<String, RateLimitConfig> {
    private Integer global;

    public Integer getGlobal() {
        return global;
    }

    public void setGlobal(Integer global) {
        this.global = global;
    }

    @Override
    public RateLimitConfig convert(String s) {
        System.out.println(s);
        return null;
    }
}
