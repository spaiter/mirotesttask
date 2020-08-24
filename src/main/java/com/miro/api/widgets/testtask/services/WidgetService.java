package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.entities.WidgetEntity;

import java.util.List;

public interface WidgetService {
    WidgetEntity createWidget(Integer xCoordinate, Integer yCoordinate, Integer z, Integer height, Integer width);
    WidgetEntity getWidgetById(String id);
    WidgetEntity updateWidgetById(String id, Integer xCoordinate, Integer yCoordinate, Integer z, Integer height, Integer width);
    Boolean deleteWidgetById(String id);
    List<WidgetEntity> getAllWidgets();
}
