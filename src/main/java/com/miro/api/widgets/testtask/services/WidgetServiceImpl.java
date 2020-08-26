package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.entities.WidgetCreateParamsHelperEntity;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.entities.WidgetUpdateParamsHelperEntity;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import com.miro.api.widgets.testtask.repositories.ShiftableIntIndexEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;

/**
 * Widget service that implements all WidgetService interface contracts.
 */
@Service
public class WidgetServiceImpl implements WidgetService {
    private final ShiftableIntIndexEntityRepository<WidgetEntity, WidgetCreateParamsHelperEntity> widgetsRepository;

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
    public WidgetEntity createAndSaveWidget(WidgetCreateParamsHelperEntity params) throws IllegalArgumentException {
        checkWidthAndHeightForNegativeValue(params.getHeight(), params.getWidth());
        long stamp = lock.writeLock();
        try {
            Integer zIndex = params.getZIndex();
            if (zIndex != null) {
                if (widgetsRepository.isNeedToShift(zIndex)) {
                    widgetsRepository.shiftUpwards(zIndex);
                }
            } else {
                params.setZIndex(widgetsRepository.getMaxIndex() + 1);
            }
            WidgetEntity widget = widgetsRepository.createEntity(params);
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
    public Optional<WidgetEntity> updateWidgetById(String id, WidgetUpdateParamsHelperEntity params) {
        checkWidthAndHeightForNegativeValue(params.getHeight(), params.getWidth());
        long stamp = lock.writeLock();
        try {
            Optional<WidgetEntity> widget = getWidgetById(id);
            widget.map(w -> {
                w.setXCoordinate(params.getXCoordinate());
                w.setYCoordinate(params.getYCoordinate());
                w.setZIndex(params.getZIndex());
                w.setHeight(params.getHeight());
                w.setWidth(params.getWidth());
                if (widgetsRepository.isNeedToShift(params.getZIndex())) {
                    widgetsRepository.shiftUpwards(params.getZIndex());
                }
                widgetsRepository.saveEntity(w);
                return w;
            });
            return widget;
        } finally {
            lock.unlockWrite(stamp);
        }
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
