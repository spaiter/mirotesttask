package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.entities.WidgetEntity;

import java.util.List;

public interface WidgetService {
    WidgetEntity createWidget(int xCoordinate, int yCoordinate, int z, int height, int width);
    WidgetEntity getWidgetById(String id);
    WidgetEntity updateWidgetById(String id, int xCoordinate, int yCoordinate, int z, int height, long width);
    Boolean deleteWidgetById(String id);
    List<WidgetEntity> getAllWidgets();
}
