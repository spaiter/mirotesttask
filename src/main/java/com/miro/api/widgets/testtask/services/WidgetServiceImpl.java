package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.dto.WidgetUpdateDTO;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import com.miro.api.widgets.testtask.repositories.ShiftableIntIndexEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

/**
 * Widget service that implements all WidgetService interface contracts.
 */
@Service
public class WidgetServiceImpl implements WidgetService<WidgetResponseDTO> {
    private final ShiftableIntIndexEntityRepository<WidgetEntity, WidgetCreateDTO> widgetsRepository;

    /**
     * Stamped lock using for atomic concurrent read / write operations on widgetsIdsToIndexesStorage and widgetsStorage.
     */
    private final StampedLock lock = new StampedLock();

    public WidgetServiceImpl(MapBasedWidgetEntityRepository repository) {
        widgetsRepository = repository;
    }

    private WidgetResponseDTO convertWidgetEntityToWidgetResponseDTO(WidgetEntity widgetEntity) {
        return new WidgetResponseDTO(
                widgetEntity.getId(),
                widgetEntity.getXCoordinate(),
                widgetEntity.getYCoordinate(),
                widgetEntity.getZIndex(),
                widgetEntity.getHeight(),
                widgetEntity.getHeight(),
                widgetEntity.getUpdatedAt()
        );
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
    public WidgetResponseDTO createAndSaveWidget(WidgetCreateDTO createDTO) throws IllegalArgumentException {
        checkWidthAndHeightForNegativeValue(createDTO.getHeight(), createDTO.getWidth());
        long stamp = lock.writeLock();
        try {
            Integer zIndex = createDTO.getZIndex();
            if (zIndex != null) {
                if (widgetsRepository.isNeedToShift(zIndex)) {
                    widgetsRepository.shiftUpwards(zIndex);
                }
            } else {
                createDTO.setZIndex(widgetsRepository.getMaxIndex() + 1);
            }
            WidgetEntity widget = widgetsRepository.createEntity(createDTO);
            widgetsRepository.saveEntity(widget);
            return convertWidgetEntityToWidgetResponseDTO(widget);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Optional<WidgetResponseDTO> getWidgetById(String id) {
        long stamp = lock.tryOptimisticRead();
        Optional<WidgetEntity> widget = widgetsRepository.findEntityById(id);

        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return widgetsRepository.findEntityById(id).map(this::convertWidgetEntityToWidgetResponseDTO);
            } finally {
                lock.unlock(stamp);
            }
        }
        return widget.map(this::convertWidgetEntityToWidgetResponseDTO);
    }

    @Override
    public Optional<WidgetResponseDTO> updateWidgetById(String id, WidgetUpdateDTO updateDTO) {
        checkWidthAndHeightForNegativeValue(updateDTO.getHeight(), updateDTO.getWidth());
        long stamp = lock.writeLock();
        try {
            Optional<WidgetEntity> widget = widgetsRepository.findEntityById(id);
            widget.map(w -> {
                w.setXCoordinate(updateDTO.getXCoordinate());
                w.setYCoordinate(updateDTO.getYCoordinate());
                w.setZIndex(updateDTO.getZIndex());
                w.setHeight(updateDTO.getHeight());
                w.setWidth(updateDTO.getWidth());
                if (widgetsRepository.isNeedToShift(updateDTO.getZIndex())) {
                    widgetsRepository.shiftUpwards(updateDTO.getZIndex());
                }
                widgetsRepository.saveEntity(w);
                return w;
            });
            return widget.map(this::convertWidgetEntityToWidgetResponseDTO);
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
    public List<WidgetResponseDTO> getAllWidgets() {
        long stamp = lock.tryOptimisticRead();
        List<WidgetEntity> widgets = widgetsRepository.findAllEntities();

        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return widgetsRepository
                        .findAllEntities()
                        .stream()
                        .map(this::convertWidgetEntityToWidgetResponseDTO)
                        .collect(Collectors.toList());
            } finally {
                lock.unlock(stamp);
            }
        }
        return widgets
                .stream()
                .map(this::convertWidgetEntityToWidgetResponseDTO)
                .collect(Collectors.toList());
    }
}
