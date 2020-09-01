package com.miro.api.widgets.testtask.config;

import com.miro.api.widgets.testtask.interceptors.RateLimitInterceptor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class AppConfigurer implements WebMvcConfigurer {
    @Lazy
    private final RateLimitInterceptor interceptor;

    public AppConfigurer(RateLimitInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/widgets/**");
    }
}
