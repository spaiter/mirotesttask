package com.miro.api.widgets.testtask.utils;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.Page;

public class PageObjectMapperModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public PageObjectMapperModule() {
        addDeserializer(Page.class, new PageDeserializer());
    }
}