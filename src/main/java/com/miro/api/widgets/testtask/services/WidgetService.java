package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.entities.WidgetCreateParamsHelperEntity;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import com.miro.api.widgets.testtask.entities.WidgetUpdateParamsHelperEntity;

import java.util.List;
import java.util.Optional;

/**
 * Common widget service interface with all business logic. Allows to manage widgets by providing CRUD operations using widgets.
 */
public interface WidgetService {

    /**
     * Allow to create widget by widget constructor params object and save it.
     * @param params {@link WidgetCreateParamsHelperEntity} Object with all necessary params to create widget. Not {@code null}
     * @return just created widget with specified parameters.
     * @throws IllegalArgumentException throws if height or width params are negative.
     */
    WidgetEntity createAndSaveWidget(WidgetCreateParamsHelperEntity params) throws IllegalArgumentException;

    /**
     * Allow to find widget by its ID. Returns empty Optional if widget not exists, otherwise returns Optional with widget.
     * @param id {@link WidgetEntity} unique ID.
     * @return {@link Optional<WidgetEntity>}
     */
    Optional<WidgetEntity> getWidgetById(String id);

    /**
     * Allow to update widget by its ID.
     * @param id {@link WidgetEntity} unique ID.
     * @param updateParamsHelperEntity {@link WidgetUpdateParamsHelperEntity} Object with all necessary params to update widget. Not {@code null}
     * @return {@link WidgetEntity}
     * @throws IllegalArgumentException throws if height or width params are negative.
     */
    Optional<WidgetEntity> updateWidgetById(String id, WidgetUpdateParamsHelperEntity updateParamsHelperEntity) throws IllegalArgumentException;

    /**
     * @param id Allow to delete widget by its unique ID.
     * @return True if widget was deleted, false if widget with such id doesn't exist.
     */
    boolean deleteWidgetById(String id);

    /**
     * Allow to get all widgets, sorted ascend by z-index.
     * @return {@link List<WidgetEntity>}
     */
    List<WidgetEntity> getAllWidgets();
}
