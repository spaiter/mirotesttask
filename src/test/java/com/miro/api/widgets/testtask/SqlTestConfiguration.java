package com.miro.api.widgets.testtask;

import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.repositories.SqlWidgetEntityRepository;
import com.miro.api.widgets.testtask.services.WidgetService;
import com.miro.api.widgets.testtask.services.WidgetSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SqlTestConfiguration {
    @Autowired
    private SqlWidgetEntityRepository repository;

    @Bean
    WidgetService<WidgetResponseDTO> widgetService() {
        return new WidgetSqlService(repository);
    }
}
