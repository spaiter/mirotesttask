package com.miro.api.widgets.testtask.config;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EndpointRateLimit {
    private String method;
    private String path;
    private Integer limit;
    private List<String> params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getLabel() {
        return EndpointRateLimit.getLabel(getMethod(), getPath(), getParams());
    }

    public static String getLabel(String method, String path, List<String> params) {
        String sortedParams = params != null ? params.stream().sorted().collect(Collectors.joining(", ")) : "";
        return method + " | " + path + " | " + sortedParams;
    }
}
