package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.entities.WidgetEntity;

import java.util.*;
import java.util.concurrent.locks.StampedLock;

public class MapBasedWidgetEntityRepository implements EntityRepository<WidgetEntity> {
    /**
     * Hash map that is store widgets ids to their indexes.
     */
    private final HashMap<String, Integer> widgetsIdsToIndexesStorage = new HashMap<>();

    /**
     * Tree map that is store widgets sorted descending by z-index.
     */
    private final TreeMap<Integer, WidgetEntity> widgetsStorage = new TreeMap<>(Collections.reverseOrder());

    /**
     * Stamped lock using for atomic concurrent read / write operations on widgetsIdsToIndexesStorage and widgetsStorage.
     */
    private final StampedLock lock = new StampedLock();

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
     * Shifts widgets z-indexes upwards.
     * @param zIndex new widget z-index.
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

    /**
     * Allow to find widget from repository by unique ID.
     * @param id {@link WidgetEntity} Widget unique ID.
     * @return {@link WidgetEntity} Widget entity if exists.
     */
    private Optional<WidgetEntity> findEntityByIdWithLock(String id) {
        Integer zIndex = this.getWidgetZIndexById(id);
        return Optional.ofNullable(widgetsStorage.get(zIndex));
    }

    /**
     * Allow to find widget from repository by unique ID with reading lock.
     * @param id {@link WidgetEntity} Widget unique ID.
     * @return {@link WidgetEntity} Widget entity if exists.
     */
    @Override
    public Optional<WidgetEntity> findEntityById(String id) {
        long stamp = lock.tryOptimisticRead();
        Optional<WidgetEntity> value = findEntityByIdWithLock(id);

        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return findEntityByIdWithLock(id);
            } finally {
                lock.unlock(stamp);
            }
        }
        return value;
    }

    /**
     * Allow to get all widgets in repository.
     * @return {@link List<WidgetEntity>} All widgets in repository.
     */
    private List<WidgetEntity> findAllEntitiesWithLock() {
        return new ArrayList<>(widgetsStorage.values());
    }

    /**
     * Allow to get all widgets in repository with reading lock.
     * @return {@link List<WidgetEntity>} All widgets in repository.
     */
    @Override
    public List<WidgetEntity> findAllEntities() {
        long stamp = lock.tryOptimisticRead();
        List<WidgetEntity> value = findAllEntitiesWithLock();

        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return findAllEntitiesWithLock();
            } finally {
                lock.unlock(stamp);
            }
        }
        return value;
    }

    /**
     * Allow to save (upsert) widget in repository with write lock.
     * @param widgetEntity {@link List<WidgetEntity>} Widget entity to save (upsert).
     */
    @Override
    public void saveEntity(WidgetEntity widgetEntity) {
        long stamp = lock.writeLock();
        try {
            int zIndex = widgetEntity.getZIndex();
            boolean needToShift = isNeedToShift(zIndex);
            if (needToShift) {
                shiftUpwards(zIndex);
            }
            widgetsStorage.put(zIndex, widgetEntity);
            widgetsIdsToIndexesStorage.put(widgetEntity.getId(), zIndex);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * Allow to remove widget by its ID from repository.
     * @param id {@link List<WidgetEntity>} Widget unique ID.
     * @return True if widget was found and removed successfully, else false.
     */
    @Override
    public boolean deleteEntityById(String id) {
        long stamp = lock.writeLock();
        try {
            int zIndex = this.getWidgetZIndexById(id);
            widgetsIdsToIndexesStorage.remove(id);
            return widgetsStorage.remove(zIndex) != null;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
}
