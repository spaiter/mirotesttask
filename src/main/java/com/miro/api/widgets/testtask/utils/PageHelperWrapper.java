package com.miro.api.widgets.testtask.utils;

import java.util.List;

public class PageHelperWrapper<T> {
    private final List<T> data;
    private final int count;

    public PageHelperWrapper(List<T> data, int count) {
        this.data = data;
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public int getCount() {
        return count;
    }
}
