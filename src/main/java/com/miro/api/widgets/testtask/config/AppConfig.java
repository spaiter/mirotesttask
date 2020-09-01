package com.miro.api.widgets.testtask.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application configuration class. Allow to add external beans and configure app.
 */
@Component
@Configuration
public class AppConfig extends ReloadableProperties implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new RateLimitConfig());
    }

    public String getTest() {
        return this.environment.getProperty("test");
    }
    public String anotherDynamicProperty() {
        return environment.getProperty("another.dynamic.prop");
    }
    @Override
    protected void propertiesReloaded() {
        System.out.println("config reloaded");
    }
}