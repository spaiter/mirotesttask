package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.dto.AppLayerDTO;
import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.dto.WidgetFilterDTO;
import com.miro.api.widgets.testtask.dto.WidgetUpdateDTO;
import com.miro.api.widgets.testtask.entities.WidgetCustomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Common widget service interface with all business logic. Allows to manage widgets by providing CRUD operations using widgets.
 */
public interface WidgetService<Response extends AppLayerDTO> {

    /**
     * Allow to create widget by widget constructor params object and save it.
     * @param createDTO {@link WidgetCreateDTO} Object with all necessary params to create widget. Not {@code null}
     * @return just created widget with specified parameters.
     * @throws IllegalArgumentException throws if height or width params are negative.
     */
    Response createAndSaveWidget(WidgetCreateDTO createDTO) throws IllegalArgumentException;

    /**
     * Allow to find widget by its ID. Returns empty Optional if widget not exists, otherwise returns Optional with widget.
     * @param id {@link WidgetCustomEntity} unique ID.
     * @return {@link Optional< WidgetCustomEntity >}
     */
    Optional<Response> getWidgetById(String id);

    /**
     * Allow to update widget by its ID.
     * @param id {@link Response} unique ID.
     * @param updateDTO {@link WidgetUpdateDTO} Object with all necessary params to update widget. Not {@code null}
     * @return {@link Response}
     * @throws IllegalArgumentException throws if height or width params are negative.
     */
    Optional<Response> updateWidgetById(String id, WidgetUpdateDTO updateDTO) throws IllegalArgumentException;

    /**
     * @param id Allow to delete widget by its unique ID.
     * @return True if widget was deleted, false if widget with such id doesn't exist.
     */
    boolean deleteWidgetById(String id);

    /**
     * Allow to get all widgets, sorted ascend by z-index.
     * @return {@link List<Response>}
     */
    List<Response> getAllWidgets();


    /**
     * Allow to get widgets with pagination, sorted ascend by z-index.
     * @param pageRequest {@link Pageable} any object that implements Pageable interface.
     * @return {@link Page<Response>}
     */
    Page<Response> getAllWidgets(Pageable pageRequest);

    /**
     * Allow to get filtered widgets with pagination, sorted ascend by z-index.
     * @param pageRequest {@link Pageable} any object that implements Pageable interface.
     * @param filterDTO DTO {@link WidgetFilterDTO} with filtering properties.
     * @return {@link Page<Response>}
     */
    Page<Response> getFilteredWidgets(Pageable pageRequest, WidgetFilterDTO filterDTO);
}
