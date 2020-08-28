package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.dto.AppLayerDTO;
import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.dto.WidgetUpdateDTO;
import com.miro.api.widgets.testtask.entities.WidgetEntity;

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
     * @param id {@link WidgetEntity} unique ID.
     * @return {@link Optional<WidgetEntity>}
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
}
