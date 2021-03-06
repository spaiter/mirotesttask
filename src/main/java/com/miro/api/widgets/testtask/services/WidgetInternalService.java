package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.dto.WidgetFilterDTO;
import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.dto.WidgetUpdateDTO;
import com.miro.api.widgets.testtask.entities.WidgetCustomEntity;
import com.miro.api.widgets.testtask.repositories.MapBasedWidgetEntityRepository;
import com.miro.api.widgets.testtask.repositories.InternalWidgetEntityRepository;
import com.miro.api.widgets.testtask.utils.PageHelperWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

/**
 * Widget service that implements all WidgetService interface contracts.
 */
@Service
@ConditionalOnProperty(value = "beans.widgetservice.impl", havingValue = "WidgetInternalService", matchIfMissing = true)
public class WidgetInternalService implements WidgetService<WidgetResponseDTO> {

    private final InternalWidgetEntityRepository<WidgetCustomEntity, WidgetCreateDTO, WidgetFilterDTO> widgetsRepository;

    /**
     * Stamped lock using for atomic concurrent read / write operations on widgetsIdsToIndexesStorage and widgetsStorage.
     */
    private final StampedLock lock = new StampedLock();

    public WidgetInternalService(MapBasedWidgetEntityRepository repository) {
        widgetsRepository = repository;
    }

    private WidgetResponseDTO convertWidgetEntityToWidgetResponseDTO(WidgetCustomEntity widgetEntity) {
        return new WidgetResponseDTO(
                widgetEntity.getId(),
                widgetEntity.getXCoordinate(),
                widgetEntity.getYCoordinate(),
                widgetEntity.getZIndex(),
                widgetEntity.getHeight(),
                widgetEntity.getWidth(),
                widgetEntity.getUpdatedAt()
        );
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
            WidgetCustomEntity widget = widgetsRepository.createEntity(createDTO);
            widgetsRepository.saveEntity(widget);
            return convertWidgetEntityToWidgetResponseDTO(widget);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Optional<WidgetResponseDTO> getWidgetById(String id) {
        long stamp = lock.tryOptimisticRead();
        Optional<WidgetCustomEntity> widget = widgetsRepository.findEntityById(id);

        if (!lock.validate(stamp)) {
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
            Optional<WidgetCustomEntity> widget = widgetsRepository.findEntityById(id);
            widget.map(w -> {
                WidgetCustomEntity widgetEntity = new WidgetCustomEntity(
                        updateDTO.getXCoordinate(),
                        updateDTO.getYCoordinate(),
                        updateDTO.getZIndex(),
                        updateDTO.getHeight(),
                        updateDTO.getWidth()
                );
                widgetEntity.setId(w.getId());
                if (widgetsRepository.isNeedToShift(updateDTO.getZIndex(), w.getId())) {
                    widgetsRepository.shiftUpwards(updateDTO.getZIndex());
                }
                widgetsRepository.deleteEntityById(w.getId());
                widgetsRepository.saveEntity(widgetEntity);
                return widgetEntity;
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
        List<WidgetCustomEntity> widgets = widgetsRepository.findAllEntities();

        if (!lock.validate(stamp)) {
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

    @Override
    public Page<WidgetResponseDTO> getAllWidgets(Pageable pageRequest) {
        long stamp = lock.tryOptimisticRead();

        int page = pageRequest.getPageNumber();
        int size = pageRequest.getPageSize();

        PageHelperWrapper<WidgetCustomEntity> widgets = widgetsRepository.findAllEntities(page, size);

        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                widgets = widgetsRepository.findAllEntities(page, size);
                List<WidgetResponseDTO> widgetResponses = widgets
                        .getData()
                        .stream()
                        .map(this::convertWidgetEntityToWidgetResponseDTO)
                        .collect(Collectors.toList());
                return new PageImpl<>(widgetResponses, pageRequest, widgets.getCount());
            } finally {
                lock.unlock(stamp);
            }
        }

        List<WidgetResponseDTO> widgetResponses = widgets
                .getData()
                .stream()
                .map(this::convertWidgetEntityToWidgetResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(widgetResponses, pageRequest, widgets.getCount());
    }

    /**
     * Allow to get filtered widgets with pagination, sorted ascend by z-index.
     *
     * @param pageRequest {@link Pageable} any object that implements Pageable interface.
     * @param filterDTO   DTO {@link WidgetFilterDTO} with filtering properties.
     * @return {@link Page<WidgetResponseDTO>}
     */
    @Override
    public Page<WidgetResponseDTO> getFilteredWidgets(Pageable pageRequest, WidgetFilterDTO filterDTO) {
        long stamp = lock.tryOptimisticRead();

        int page = pageRequest.getPageNumber();
        int size = pageRequest.getPageSize();

        PageHelperWrapper<WidgetCustomEntity> widgets = widgetsRepository.getFilteredEntities(page, size, filterDTO);

        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                widgets = widgetsRepository.getFilteredEntities(page, size, filterDTO);
                List<WidgetResponseDTO> widgetResponses = widgets
                        .getData()
                        .stream()
                        .map(this::convertWidgetEntityToWidgetResponseDTO)
                        .collect(Collectors.toList());
                return new PageImpl<>(widgetResponses, pageRequest, widgets.getCount());
            } finally {
                lock.unlock(stamp);
            }
        }

        List<WidgetResponseDTO> widgetResponses = widgets
                .getData()
                .stream()
                .map(this::convertWidgetEntityToWidgetResponseDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(widgetResponses, pageRequest, widgets.getCount());
    }

    /**
     * Allow to clean repository.
     */
    @Override
    public void purge() {
        widgetsRepository.purge();
    }
}
