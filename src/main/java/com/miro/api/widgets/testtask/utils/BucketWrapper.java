package com.miro.api.widgets.testtask.utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import java.time.Duration;

public class BucketWrapper {
    private final Integer limit;
    private final Bucket bucket;

    public BucketWrapper(Integer limit) {
        this.limit = limit;
        Bandwidth bandwidth =  Bandwidth.classic(this.limit, Refill.intervally(this.limit, Duration.ofMinutes(1)));;
        this.bucket = Bucket4j.builder().addLimit(bandwidth).build();;
    }

    public Integer getLimit() {
        return limit;
    }

    public Bucket getBucket() {
        return bucket;
    }
}
