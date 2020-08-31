package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.entities.WidgetEntity;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Repository
public class MapBasedWidgetEntityRepository implements ShiftableIntIndexEntityRepository<WidgetEntity, WidgetCreateDTO> {
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
        return widgetsStorage.containsKey(index);
    }

    /**
     * Return z-index for tail position in repository for filter widgets to shift.
     * @param index z-index to insert in repository.
     * @return z-index shifting to.
     */
    private int getTailToIndex(int index) {
        int currentListPosition;
        int elementsCountNeedToReachCurrentValue;
        int realElementsCount;
        int listSize;
        List<Integer> widgetsList;
        widgetsList = new ArrayList<>(widgetsStorage.headMap(index, true).keySet());
        final int realListSize = widgetsList.size();
        listSize = widgetsList.size();
        boolean resetCurrentValue = true;
        Integer currentListValue = null;

        while (listSize > 1) {
            currentListPosition = listSize / 2;
            if (currentListValue != null && currentListValue + 1 != widgetsList.get(listSize - 1)) {
                widgetsList = widgetsList.subList(currentListPosition, currentListPosition + 1);
                if (widgetsList.get(0) != index) {
                    resetCurrentValue = false;
                }
                break;
            }
            currentListValue = widgetsList.get(currentListPosition);
            if (currentListValue == index && widgetsList.get(currentListPosition - 1) != currentListValue + 1) {
                widgetsList = widgetsList.subList(currentListPosition, currentListPosition + 1);
                break;
            }
            elementsCountNeedToReachCurrentValue = currentListValue - index;
            realElementsCount = realListSize - currentListPosition - 1;
            if (realElementsCount >= elementsCountNeedToReachCurrentValue) {
                widgetsList = widgetsList.subList(0, currentListPosition);
            } else {
                widgetsList = widgetsList.subList(currentListPosition + 1, widgetsList.size());
            }
            listSize = widgetsList.size();
        }
        if (resetCurrentValue) {
            currentListValue = widgetsList.get(0);
        }
        return currentListValue;
    }

    /**
     * Shifts only necessary widgets z-indexes upwards.
     * @param index new widget z-index.
     */
    public void shiftUpwards(int index) {
        int tailTo = getTailToIndex(index);
        widgetsStorage
                .headMap(index, true)
                .tailMap(tailTo, true)
                .values()
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
     * @param createDTO Object with all necessary params to create widget entity.
     * @return widget entity created by constructor params object.
     */
    @Override
    public WidgetEntity createEntity(WidgetCreateDTO createDTO) {
        return new WidgetEntity(createDTO);
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
     * Allow to get all widgets in repository by page.
     * @param page Page of widgets you want to get. Min value is 1.
     * @param size Number of widgets in page. Min value is 1.
     * @return {@link List<WidgetEntity>} Widgets in repository on page.
     */
    public List<WidgetEntity> findAllEntities(int page, int size) {
        return widgetsStorage
                .descendingMap()
                .values()
                .stream()
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
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

    /**
     * Allow to remove all widgets from repository.
     */
    @Override
    public void purge() {
        widgetsStorage.clear();
        widgetsIdsToZIndexesStorage.clear();
    }

    /**
     * Allow to get all widgets count.
     * @return count of all widgets.
     */
    @Override
    public int getCount() {
        return widgetsStorage.size();
    }
}
