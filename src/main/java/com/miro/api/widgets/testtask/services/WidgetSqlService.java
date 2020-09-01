package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.dto.WidgetFilterDTO;
import com.miro.api.widgets.testtask.dto.WidgetResponseDTO;
import com.miro.api.widgets.testtask.dto.WidgetUpdateDTO;
import com.miro.api.widgets.testtask.entities.WidgetJpaEntity;
import com.miro.api.widgets.testtask.repositories.SqlWidgetEntityRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@ConditionalOnProperty(value="beans.widgetservice.impl", havingValue = "WidgetSqlService", matchIfMissing = false)
public class WidgetSqlService implements WidgetService<WidgetResponseDTO> {

    private final SqlWidgetEntityRepository widgetsRepository;

    private WidgetResponseDTO convertWidgetEntityToWidgetResponseDTO(WidgetJpaEntity widgetEntity) {
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

    public WidgetSqlService(SqlWidgetEntityRepository widgetsRepository) {
        this.widgetsRepository = widgetsRepository;
    }

    @Override
    @Transactional
    public WidgetResponseDTO createAndSaveWidget(WidgetCreateDTO createDTO) throws IllegalArgumentException {
        checkWidthAndHeightForNegativeValue(createDTO.getHeight(), createDTO.getWidth());
        WidgetJpaEntity widgetEntity = new WidgetJpaEntity(createDTO);
        if (widgetsRepository.isNeedToShift(widgetEntity.getZIndex())) {
            widgetsRepository.shiftUpwards(widgetEntity.getZIndex());
        }
        widgetEntity = widgetsRepository.save(widgetEntity);
        return convertWidgetEntityToWidgetResponseDTO(widgetEntity);
    }

    @Override
    @Transactional
    public Optional<WidgetResponseDTO> getWidgetById(String id) {
        return widgetsRepository.findById(id).map(this::convertWidgetEntityToWidgetResponseDTO);
    }

    @Override
    @Transactional
    public Optional<WidgetResponseDTO> updateWidgetById(String id, WidgetUpdateDTO updateDTO) throws IllegalArgumentException {
        checkWidthAndHeightForNegativeValue(updateDTO.getHeight(), updateDTO.getWidth());
        Optional<WidgetJpaEntity> widgetEntity = widgetsRepository.findById(id);
        return widgetEntity.map(widget -> {
            widget.setXCoordinate(updateDTO.getXCoordinate());
            widget.setYCoordinate(updateDTO.getYCoordinate());
            widget.setWidth(updateDTO.getWidth());
            widget.setHeight(updateDTO.getHeight());
            widget.setZIndex(updateDTO.getZIndex());
            widget.markUpdated();
            if (widgetsRepository.isNeedToShift(widget.getZIndex(), widget.getId())) {
                widgetsRepository.shiftUpwards(widget.getZIndex());
            }
            widgetsRepository.save(widget);
            return convertWidgetEntityToWidgetResponseDTO(widget);
        });
    }

    @Override
    @Transactional
    public boolean deleteWidgetById(String id) {
        Optional<WidgetJpaEntity> widgetEntity = widgetsRepository.findById(id);
        widgetsRepository.deleteById(id);
        return widgetEntity.isPresent();
    }

    @Override
    @Transactional
    public List<WidgetResponseDTO> getAllWidgets() {
        return StreamSupport.stream(widgetsRepository.findAll().spliterator(), false)
                .map(this::convertWidgetEntityToWidgetResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<WidgetResponseDTO> getAllWidgets(Pageable pageRequest) {
        Page<WidgetJpaEntity> widgetJpaEntityPage = widgetsRepository.findAll(pageRequest);
        List<WidgetResponseDTO> responseDTOList = widgetJpaEntityPage.getContent().stream().map(this::convertWidgetEntityToWidgetResponseDTO).collect(Collectors.toList());
        return new PageImpl<>(responseDTOList, pageRequest, widgetJpaEntityPage.getTotalElements());
    }

    @Override
    @Transactional
    public Page<WidgetResponseDTO> getFilteredWidgets(Pageable pageRequest, WidgetFilterDTO filterDTO) {
        Page<WidgetJpaEntity> widgetJpaEntityPage = widgetsRepository.getFilteredEntities(
                filterDTO.getX1(),
                filterDTO.getY1(),
                filterDTO.getX2(),
                filterDTO.getY2(),
                pageRequest
        );
        List<WidgetResponseDTO> responseDTOList = widgetJpaEntityPage.getContent().stream().map(this::convertWidgetEntityToWidgetResponseDTO).collect(Collectors.toList());
        return new PageImpl<>(responseDTOList, pageRequest, widgetJpaEntityPage.getTotalElements());
    }

    /**
     * Allow to clean repository.
     */
    @Override
    public void purge() {
        widgetsRepository.deleteAll();
    }
}
