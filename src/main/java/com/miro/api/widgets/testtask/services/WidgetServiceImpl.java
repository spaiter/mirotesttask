package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.repositories.EntityRepository;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;

import java.util.List;
import java.util.Optional;

/**
 * Widget service that implements all WidgetService interface contracts.
 */
public class WidgetServiceImpl implements WidgetService {
    private final EntityRepository<WidgetEntity> widgetsRepository;

    public WidgetServiceImpl(MapBasedWidgetEntityRepository repository) {
        widgetsRepository = repository;
    }

    private void checkWidthAndHeightForNegativeValue(int height, int width) throws IllegalArgumentException {
        if (height < 0) {
            throw new IllegalArgumentException("Widget height can't be negative.");
        }
        if (width < 0) {
            throw new IllegalArgumentException("Widget width can't be negative.");
        }
    }

    @Override
    public WidgetEntity createWidget(int xCoordinate, int yCoordinate, Integer zIndex, int height, int width) throws IllegalArgumentException {
        checkWidthAndHeightForNegativeValue(height, width);
        WidgetEntity widget = new WidgetEntity(xCoordinate, yCoordinate, zIndex, height, width);
        widgetsRepository.saveEntity(widget);
        return widget;
    }

    @Override
    public Optional<WidgetEntity> getWidgetById(String id) {
        return widgetsRepository.findEntityById(id);
    }

    @Override
    public Optional<WidgetEntity> updateWidgetById(String id, int xCoordinate, int yCoordinate, int zIndex, int height, int width) {
        checkWidthAndHeightForNegativeValue(height, width);
        Optional<WidgetEntity> widget = getWidgetById(id);
        widget.map(w -> {
            w.setXCoordinate(xCoordinate);
            w.setYCoordinate(yCoordinate);
            w.setZIndex(zIndex);
            w.setHeight(height);
            w.setWidth(width);
            return w;
        });
        return widget;
    }

    @Override
    public Boolean deleteWidgetById(String id) {
        return widgetsRepository.deleteEntityById(id);
    }

    @Override
    public List<WidgetEntity> getAllWidgets() {
        return widgetsRepository.findAllEntities();
    }
}
