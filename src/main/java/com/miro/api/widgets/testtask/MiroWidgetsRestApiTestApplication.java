package com.miro.api.widgets.testtask;

import com.miro.api.widgets.testtask.interceptors.RateLimitInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MiroWidgetsRestApiTestApplication implements WebMvcConfigurer {

    @Lazy
    private final RateLimitInterceptor interceptor;

    public MiroWidgetsRestApiTestApplication(RateLimitInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/widgets/**");
    }

    public static void main(String[] args) {
        SpringApplication.run(MiroWidgetsRestApiTestApplication.class, args);
    }

}
