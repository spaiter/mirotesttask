package com.miro.api.widgets.testtask.interceptors;

import com.miro.api.widgets.testtask.config.AppConfig;
import com.miro.api.widgets.testtask.config.EndpointRateLimit;
import com.miro.api.widgets.testtask.utils.BucketWrapper;
import io.github.bucket4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final AppConfig config;

    private final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));

    private final ConcurrentHashMap<String, BucketWrapper> bucketsMap = new ConcurrentHashMap<>();
    private final BucketWrapper globalRateLimitBucket;

    public RateLimitInterceptor(AppConfig config) {
        this.config = config;
        Integer globalRateLimit = this.config.getRateLimitConfig().getGlobal();
        this.globalRateLimitBucket = globalRateLimit != 0 ? generateBucketWithLimit(globalRateLimit) : null;
    }

    private BucketWrapper generateBucketWithLimit(Integer limit) {
        return new BucketWrapper(limit);
    }

    private BucketWrapper createBucketAndPutToMap(String label, Integer limit) {
        BucketWrapper newBucket = new BucketWrapper(limit);
        bucketsMap.put(label, newBucket);
        return newBucket;
    }

    private BucketWrapper getBucket(String label, Integer limit) {
        BucketWrapper existingBucket = bucketsMap.get(label);
        if (existingBucket != null) {
            Integer currentBucketCapacity = existingBucket.getLimit();
            if (!currentBucketCapacity.equals(limit)) {
                return createBucketAndPutToMap(label, limit);
            }
            return existingBucket;
        }
        return createBucketAndPutToMap(label, limit);
    }

    private BucketWrapper getBucketForEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        List<String> params = new ArrayList<>(request.getParameterMap().keySet());
        String labelForCurrentRequest = EndpointRateLimit.getLabel(method, uri, params);

        Map<String, Integer> rateLimitConfigs = config
                .getRateLimitConfig()
                .getEndpoints()
                .stream()
                .collect(Collectors.toMap(EndpointRateLimit::getLabel, EndpointRateLimit::getLimit));

        Integer limit = rateLimitConfigs.get(labelForCurrentRequest);
        if (limit != null) {
            return limit != 0 ? getBucket(labelForCurrentRequest, limit) : null;
        }
        return globalRateLimitBucket;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        BucketWrapper bucketWrapper = getBucketForEndpoint(request);
        if (bucketWrapper == null) {
            return true;
        }
        ConsumptionProbe probe = bucketWrapper.getBucket().tryConsumeAndReturnRemaining(1);
        response.addHeader("X-Rate-Limit", String.format("%s requests per minute", bucketWrapper.getLimit()));
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