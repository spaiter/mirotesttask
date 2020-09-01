package com.miro.api.widgets.testtask;

import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import com.miro.api.widgets.testtask.services.WidgetInternalService;
import com.miro.api.widgets.testtask.services.WidgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InternalTestConfiguration {
    @Autowired
    private MapBasedWidgetEntityRepository repository;

    @Bean
    WidgetService<WidgetResponseDTO> widgetService() {
        return new WidgetInternalService(repository);
    }
}
