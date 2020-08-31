package com.miro.api.widgets.testtask.interceptors;

import io.github.bucket4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));
    private final Bandwidth defaultRateLimit = Bandwidth.classic(1, Refill.intervally(1, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket4j.builder().addLimit(defaultRateLimit).build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        response.addHeader("X-Rate-Limit", String.format("%s requests per %s seconds", defaultRateLimit.getCapacity(), defaultRateLimit.getRefillPeriodNanos() / 1_000_000_000));
        response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
        if (probe.isConsumed()) {
            return true;
        } else {
            Instant resetDate = Instant.now().plusNanos(probe.getNanosToWaitForRefill());
            response.addHeader("X-Rate-Limit-Reset-Date", formatter.format(resetDate));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                    "You have exhausted your API Request Quota");
            return false;
        }
    }
}