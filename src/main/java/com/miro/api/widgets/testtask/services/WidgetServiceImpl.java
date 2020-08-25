package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import com.miro.api.widgets.testtask.repositories.ShiftableIntIndexEntityRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

/**
 * Widget service that implements all WidgetService interface contracts.
 */
public class WidgetServiceImpl implements WidgetService {
    private final ShiftableIntIndexEntityRepository<WidgetEntity> widgetsRepository;

    /**
     * Stamped lock using for atomic concurrent read / write operations on widgetsIdsToIndexesStorage and widgetsStorage.
     */
    private final StampedLock lock = new StampedLock();

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
        long stamp = lock.writeLock();
        try {
            if (zIndex != null) {
                if (widgetsRepository.isNeedToShift(zIndex)) {
                    widgetsRepository.shiftUpwards(zIndex);
                }
            } else {
                zIndex = widgetsRepository.getMaxIndex() + 1;
            }
            WidgetEntity widget = new WidgetEntity(xCoordinate, yCoordinate, zIndex, height, width);
            widgetsRepository.saveEntity(widget);
            return widget;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Optional<WidgetEntity> getWidgetById(String id) {
        long stamp = lock.tryOptimisticRead();
        Optional<WidgetEntity> widget = widgetsRepository.findEntityById(id);

        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return widgetsRepository.findEntityById(id);
            } finally {
                lock.unlock(stamp);
            }
        }
        return widget;
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
    public boolean deleteWidgetById(String id) {
        long stamp = lock.writeLock();
        try {
            return widgetsRepository.deleteEntityById(id);
        } finally {
            lock.unlockWrite(stamp);
        }

    }

    @Override
    public List<WidgetEntity> getAllWidgets() {
        long stamp = lock.tryOptimisticRead();
        List<WidgetEntity> widgets = widgetsRepository.findAllEntities();

        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return widgetsRepository.findAllEntities();
            } finally {
                lock.unlock(stamp);
            }
        }
        return widgets;
    }
}
