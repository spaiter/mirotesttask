package com.miro.api.widgets.testtask.services;

import com.miro.api.widgets.testtask.entities.WidgetConstructorParams;
import com.miro.api.widgets.testtask.entities.WidgetEntity;

import java.util.List;
import java.util.Optional;

/**
 * Common widget service interface with all business logic. Allows to manage widgets by providing CRUD operations using widgets.
 */
public interface WidgetService {

    /**
     * Allow to create widget by widget constructor params object and save it.
     * @param params {@link WidgetConstructorParams} Object with all necessary params to create widget. Not {@code null}
     * @return just created widget with specified parameters.
     * @throws IllegalArgumentException throws if height or width params are negative.
     */
    WidgetEntity createAndSaveWidget(WidgetConstructorParams params) throws IllegalArgumentException;

    /**
     * Allow to find widget by its ID. Returns empty Optional if widget not exists, otherwise returns Optional with widget.
     * @param id {@link WidgetEntity} unique ID.
     * @return {@link Optional<WidgetEntity>}
     */
    Optional<WidgetEntity> getWidgetById(String id);

    /**
     * Allow to update widget by its ID.
     * @param id {@link WidgetEntity} unique ID.
     * @param xCoordinate {@link WidgetEntity} x-coordinate, not {@code null}, can be negative.
     * @param yCoordinate {@link WidgetEntity} y-coordinate, not {@code null}, can be negative.
     * @param zIndex {@link WidgetEntity} z-index, not {@code null}. can be negative.
     * @param height {@link WidgetEntity} height, not {@code null}{@code null}, can't be negative.
     * @param width {@link WidgetEntity} width, not {@code null}, can't be negative.
     * @return {@link WidgetEntity}
     * @throws IllegalArgumentException throws if height or width params are negative.
     */
    Optional<WidgetEntity> updateWidgetById(String id, int xCoordinate, int yCoordinate, int zIndex, int height, int width) throws IllegalArgumentException;

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
