package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.entities.WidgetConstructorParams;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Repository
public class MapBasedWidgetEntityRepository implements ShiftableIntIndexEntityRepository<WidgetEntity, WidgetConstructorParams> {
    /**
     * Hash map that is store widgets ids to their indexes.
     */
    private final HashMap<String, Integer> widgetsIdsToZIndexesStorage = new HashMap<>();

    /**
     * Tree map that is store widgets sorted descending by z-index.
     */
    private final ConcurrentSkipListMap<Integer, WidgetEntity> widgetsStorage = new ConcurrentSkipListMap<>(Collections.reverseOrder());

    /**
     * Allow to get widget z-index by its id.
     * @param id {@link WidgetEntity} Widget unique ID.
     * @return Widget z-index mapped to its id.
     */
    private int getWidgetZIndexById(String id) {
        return widgetsIdsToZIndexesStorage.get(id);
    }

    /**
     * Check if widgets z-indexes needs to be shifted.
     * @param index {@link WidgetEntity} Widget z-index.
     * @return True if widgets z-indexes needs to be shifted, else false.
     */
    public boolean isNeedToShift(int index) {
        try {
            return widgetsStorage.firstKey() >= index;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Shifts only necessary widgets z-indexes upwards.
     * @param index new widget z-index.
     */
    public void shiftUpwards(int index) {
        widgetsStorage
                .headMap(index, true)
                .values()
                .stream()
                .filter(widget -> {
                    try {
                        int currentWidgetZIndex = widget.getZIndex();
                        int nextWidgetZIndex = widgetsStorage.higherKey(currentWidgetZIndex);
                        return currentWidgetZIndex <= nextWidgetZIndex + 1;
                    } catch (NullPointerException e) {
                        return true;
                    }
                })
                .forEach(widget -> {
                    int newWidgetZIndex = widget.getZIndex() + 1;
                    widget.setZIndex(newWidgetZIndex);
                    widget.markUpdated();
                    widgetsStorage.put(newWidgetZIndex, widget);
                    widgetsIdsToZIndexesStorage.put(widget.getId(), newWidgetZIndex);
                });
    }

    /**
     * Allow to create widget entity via object with all necessary params.
     * @param widgetConstructorParams Object with all necessary params to create widget entity.
     * @return widget entity created by constructor params object.
     */
    @Override
    public WidgetEntity createEntity(WidgetConstructorParams widgetConstructorParams) {
        return new WidgetEntity(widgetConstructorParams);
    }

    /**
     * Allow to find widget from repository by unique ID.
     * @param id {@link WidgetEntity} Widget unique ID.
     * @return {@link WidgetEntity} Widget entity if exists.
     */
    public Optional<WidgetEntity> findEntityById(String id) {
        Integer zIndex = this.getWidgetZIndexById(id);
        return Optional.ofNullable(widgetsStorage.get(zIndex));
    }

    /**
     * Allow to get all widgets in repository.
     * @return {@link List<WidgetEntity>} All widgets in repository.
     */
    public List<WidgetEntity> findAllEntities() {
        return new ArrayList<>(widgetsStorage.descendingMap().values());
    }

    /**
     * Allow to get current max z-index of all widgets.
     * @return {@link WidgetEntity} max z-index.
     */
    public int getMaxIndex() {
        try {
            return widgetsStorage.firstKey();
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    /**
     * Allow to save (upsert) widget in repository with write lock.
     * @param widgetEntity {@link List<WidgetEntity>} Widget entity to save (upsert).
     */
    @Override
    public WidgetEntity saveEntity(WidgetEntity widgetEntity) {
        int zIndex = widgetEntity.getZIndex();
        widgetsStorage.put(zIndex, widgetEntity);
        widgetsIdsToZIndexesStorage.put(widgetEntity.getId(), zIndex);
        return widgetEntity;
    }

    /**
     * Allow to remove widget by its ID from repository.
     * @param id {@link List<WidgetEntity>} Widget unique ID.
     * @return True if widget was found and removed successfully, else false.
     */
    @Override
    public boolean deleteEntityById(String id) {
        int zIndex = this.getWidgetZIndexById(id);
        widgetsIdsToZIndexesStorage.remove(id);
        return widgetsStorage.remove(zIndex) != null;
    }
}
