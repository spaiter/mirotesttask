package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.entities.WidgetEntity;

import java.util.*;
import java.util.concurrent.locks.StampedLock;

public class MapBasedWidgetEntityRepository implements EntityRepository<WidgetEntity> {
    /**
     * Hash map that is store widgets ids to their indexes.
     */
    private HashMap<String, Integer> widgetsIdsToIndexesStorage = new HashMap<>();

    /**
     * Tree map that is store widgets sorted descending by z-index.
     */
    private TreeMap<Integer, WidgetEntity> widgetsStorage = new TreeMap<>(Collections.reverseOrder());

    /**
     * Stamped lock using for atomic concurrent read / write operations on widgetsIdsToIndexesStorage and widgetsStorage.
     */
    private StampedLock lock = new StampedLock();

    /**
     * Allow to get widget z-index by its id.
     * @param id {@link WidgetEntity} Widget unique ID.
     * @return Widget z-index mapped to its id.
     */
    private int getWidgetZIndexById(String id) {
        return widgetsIdsToIndexesStorage.get(id);
    }

    /**
     * Check if widgets z-indexes needs to be shifted.
     * @param zIndex {@link WidgetEntity} Widget z-index.
     * @return True if widgets z-indexes needs to be shifted, else false.
     */
    private boolean isNeedToShift(int zIndex) {
        return widgetsStorage.lastKey() <= zIndex;
    }

    /**
     * Shifts widgets upwards.
     *
     * @param zIndex new widget z-index
     */
    private void shiftUpwards(int zIndex) {
        widgetsStorage
                .headMap(zIndex, true)
                .values()
                .forEach(widget -> {
                    int newWidgetZIndex = widget.getZIndex() + 1;
                    widget.setZIndex(newWidgetZIndex);
                    widgetsIdsToIndexesStorage.put(widget.getId(), newWidgetZIndex);
                });
    }

    @Override
    public Optional<WidgetEntity> findEntityById(String id) {
        Integer zIndex = this.getWidgetZIndexById(id);
        return Optional.ofNullable(widgetsStorage.get(zIndex));
    }

    @Override
    public List<WidgetEntity> findAllEntities() {
        return new ArrayList<>(widgetsStorage.values());
    }

    @Override
    public void saveEntity(WidgetEntity widgetEntity) {
        int zIndex = widgetEntity.getZIndex();
        boolean needToShift = isNeedToShift(zIndex);
        if (needToShift) {
            shiftUpwards(zIndex);
        }
        widgetsStorage.put(zIndex, widgetEntity);
        widgetsIdsToIndexesStorage.put(widgetEntity.getId(), zIndex);
    }

    @Override
    public boolean deleteEntityById(String id) {
        int zIndex = this.getWidgetZIndexById(id);
        widgetsIdsToIndexesStorage.remove(id);
        return widgetsStorage.remove(zIndex) != null;
    }
}
