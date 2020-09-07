package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;
import com.miro.api.widgets.testtask.dto.WidgetFilterDTO;
import com.miro.api.widgets.testtask.entities.WidgetCustomEntity;
import com.miro.api.widgets.testtask.utils.PageHelperWrapper;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Repository
public class MapBasedWidgetEntityRepository implements InternalWidgetEntityRepository<WidgetCustomEntity, WidgetCreateDTO, WidgetFilterDTO> {
    /**
     * Hash map that is store widgets ids to their indexes.
     */
    private final HashMap<String, Integer> widgetsIdsToZIndexesStorage = new HashMap<>();

    /**
     * Tree map that is store widget x-coordinate search index.
     */
    private final TreeMap<Integer, Set<Integer>> x1CoordinateSearchIndex = new TreeMap<>();

    /**
     * Tree map that is store widget y-coordinate search index.
     */
    private final TreeMap<Integer, Set<Integer>> y1CoordinateSearchIndex = new TreeMap<>();

    /**
     * Tree map that is store widget x-coordinate + widget width search index.
     */
    private final TreeMap<Integer, Set<Integer>> x2CoordinateSearchIndex = new TreeMap<>();

    /**
     * Tree map that is store widget y-coordinate + widget height search index.
     */
    private final TreeMap<Integer, Set<Integer>> y2CoordinateSearchIndex = new TreeMap<>();

    /**
     * Allow to remove widget from search index.
     *
     * @param searchIndex One of search indexes.
     * @param key         Key of search index.
     * @param zIndex      {@link WidgetCustomEntity} z-index.
     */
    private void removeWidgetFromSearchIndex(TreeMap<Integer, Set<Integer>> searchIndex, int key, int zIndex) {
        Set<Integer> zIndexes = searchIndex.get(key);
        if (zIndexes != null) {
            zIndexes.remove(zIndex);
            if (zIndexes.isEmpty()) {
                searchIndex.remove(key);
            }
        }
    }

    /**
     * Allow to add widget to search index.
     *
     * @param searchIndex One of search indexes.
     * @param key         Key of search index.
     * @param zIndex      {@link WidgetCustomEntity} z-index.
     */
    private void addWidgetToSearchIndex(TreeMap<Integer, Set<Integer>> searchIndex, int key, int zIndex) {
        Set<Integer> zIndexes = searchIndex.get(key);
        if (zIndexes == null) {
            HashSet<Integer> set = new HashSet<>();
            set.add(zIndex);
            searchIndex.put(key, set);
        } else {
            zIndexes.add(zIndex);
        }
    }

    /**
     * Allow to add widget to search indexes.
     *
     * @param widget {@link WidgetCustomEntity} which will be added to search indexes.
     */
    private void addWidgetToSearchIndexes(WidgetCustomEntity widget) {
        int zIndex = widget.getZIndex();
        int x1 = widget.getXCoordinate();
        int y1 = widget.getYCoordinate();
        int x2 = x1 + widget.getWidth();
        int y2 = y1 + widget.getHeight();

        addWidgetToSearchIndex(x1CoordinateSearchIndex, x1, zIndex);
        addWidgetToSearchIndex(y1CoordinateSearchIndex, y1, zIndex);
        addWidgetToSearchIndex(x2CoordinateSearchIndex, x2, zIndex);
        addWidgetToSearchIndex(y2CoordinateSearchIndex, y2, zIndex);
    }

    /**
     * Allow to remove widget from search indexes.
     *
     * @param widget {@link WidgetCustomEntity} which will be removed from search indexes.
     */
    private void removeWidgetFromSearchIndexes(WidgetCustomEntity widget) {
        int zIndex = widget.getZIndex();
        int x1 = widget.getXCoordinate();
        int y1 = widget.getYCoordinate();
        int x2 = x1 + widget.getWidth();
        int y2 = y1 + widget.getHeight();
        removeWidgetFromSearchIndex(x1CoordinateSearchIndex, x1, zIndex);
        removeWidgetFromSearchIndex(y1CoordinateSearchIndex, y1, zIndex);
        removeWidgetFromSearchIndex(x2CoordinateSearchIndex, x2, zIndex);
        removeWidgetFromSearchIndex(y2CoordinateSearchIndex, y2, zIndex);
    }

    /**
     * Allow to set widget indexes.
     *
     * @param oldWidget {@link WidgetCustomEntity} widget with old params.
     * @param newWidget {@link WidgetCustomEntity} widget with new params.
     */
    private void setFilteringIndexes(WidgetCustomEntity oldWidget, WidgetCustomEntity newWidget) {
        if (oldWidget != null && oldWidget.getZIndex() == newWidget.getZIndex()) {
            removeWidgetFromSearchIndexes(oldWidget);
        }

        addWidgetToSearchIndexes(newWidget);
    }

    /**
     * Tree map that is store widgets sorted descending by z-index.
     */
    private final ConcurrentSkipListMap<Integer, WidgetCustomEntity> widgetsStorage = new ConcurrentSkipListMap<>(Collections.reverseOrder());

    /**
     * Allow to get widget z-index by its id.
     *
     * @param id {@link WidgetCustomEntity} Widget unique ID.
     * @return Widget z-index mapped to its id.
     */
    private int getWidgetZIndexById(String id) {
        return widgetsIdsToZIndexesStorage.get(id);
    }

    /**
     * Check if widgets z-indexes needs to be shifted. Use on create.
     *
     * @param index {@link WidgetCustomEntity} Widget z-index.
     * @return True if widgets z-indexes needs to be shifted, else false.
     */
    public boolean isNeedToShift(int index) {
        return widgetsStorage.containsKey(index);
    }

    /**
     * Check if widgets z-indexes needs to be shifted. Use on update.
     * We need to shift widgets only if widget with such z-index has same id, so it is widget update with same z-index.
     *
     * @param zIndex {@link WidgetCustomEntity} Widget z-index.
     * @param id     {@link WidgetCustomEntity} Widget id.
     * @return True if widgets z-indexes needs to be shifted, else false.
     */
    public boolean isNeedToShift(int zIndex, String id) {
        if (!widgetsStorage.containsKey(zIndex)) {
            return false;
        }
        WidgetCustomEntity widget = widgetsStorage.get(zIndex);
        return !widget.getId().equals(id);
    }

    /**
     * Return z-index for tail position in repository for filter widgets to shift.
     * It is the z-index where difference between inserting widget z-index and searchable z-index equals size of elements between this z-indexes.
     *
     * For example, there are such z-indexes in repository: 1, 2, 3, 4, 5, 10, 11, 20, 21 and we want to insert new widget with z-index 3.
     * In this case we need to shifts only widgets with z-index 3, 4 and 5. So for this example this method will return 5.
     *
     * This method use binary search algorithm with additional logic.
     *
     *
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
     *
     * Update widgets z-indexes for widgets where z-indexes greater than insertable index and lower than index returning by getTailToIndex method.
     *
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
                    addWidgetToSearchIndexes(widget);
                    widgetsStorage.put(newWidgetZIndex, widget);
                    widgetsIdsToZIndexesStorage.put(widget.getId(), newWidgetZIndex);
                });
    }

    /**
     * Allow to create widget entity via object with all necessary params.
     *
     * @param createDTO Object with all necessary params to create widget entity.
     * @return widget entity created by constructor params object.
     */
    @Override
    public WidgetCustomEntity createEntity(WidgetCreateDTO createDTO) {
        return new WidgetCustomEntity(createDTO);
    }

    /**
     * Allow to find widget from repository by unique ID.
     *
     * @param id {@link WidgetCustomEntity} Widget unique ID.
     * @return {@link WidgetCustomEntity} Widget entity if exists.
     */
    public Optional<WidgetCustomEntity> findEntityById(String id) {
        Integer zIndex = this.getWidgetZIndexById(id);
        return Optional.ofNullable(widgetsStorage.get(zIndex));
    }

    /**
     * Allow to get all widgets in repository.
     *
     * @return {@link List<WidgetCustomEntity>} All widgets in repository.
     */
    public List<WidgetCustomEntity> findAllEntities() {
        return new ArrayList<>(widgetsStorage.descendingMap().values());
    }

    /**
     * Allow to get all widgets in repository by page.
     *
     * @param page Page of widgets you want to get. Min value is 1.
     * @param size Number of widgets in page. Min value is 1.
     * @return {@link List<WidgetCustomEntity>} Widgets in repository on page.
     */
    public PageHelperWrapper<WidgetCustomEntity> findAllEntities(int page, int size) {
        List<WidgetCustomEntity> widgets = widgetsStorage
                .descendingMap()
                .values()
                .stream()
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
        int count = widgetsStorage.size();
        return new PageHelperWrapper<>(widgets, count);
    }

    /**
     * Allow to get current max z-index of all widgets.
     *
     * @return {@link WidgetCustomEntity} max z-index.
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
     *
     * @param widgetEntity {@link List<WidgetCustomEntity>} Widget entity to save (upsert).
     */
    @Override
    public WidgetCustomEntity saveEntity(WidgetCustomEntity widgetEntity) {
        int zIndex = widgetEntity.getZIndex();
        WidgetCustomEntity oldWidgetEntity = widgetsStorage.get(zIndex);
        setFilteringIndexes(oldWidgetEntity, widgetEntity);
        widgetsStorage.put(zIndex, widgetEntity);
        widgetsIdsToZIndexesStorage.put(widgetEntity.getId(), zIndex);
        return widgetEntity;
    }

    /**
     * Allow to remove widget by its ID from repository.
     *
     * @param id {@link List<WidgetCustomEntity>} Widget unique ID.
     * @return True if widget was found and removed successfully, else false.
     */
    @Override
    public boolean deleteEntityById(String id) {
        int zIndex = this.getWidgetZIndexById(id);
        WidgetCustomEntity widget = widgetsStorage.get(zIndex);
        removeWidgetFromSearchIndexes(widget);
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
        x1CoordinateSearchIndex.clear();
        y1CoordinateSearchIndex.clear();
        x2CoordinateSearchIndex.clear();
        y2CoordinateSearchIndex.clear();
    }

    /**
     * Allow to get all widgets count.
     *
     * @return count of all widgets.
     */
    @Override
    public int getCount() {
        return widgetsStorage.size();
    }

    /**
     * Allow to get all widgets z-indexes suitable for current search index.
     *
     * @param searchIndex One of four search indexes.
     * @param filter      Filtering value for search index.
     * @param gte         If true, then search only in higher (inclusive) elements, else only lower (inclusive) elements.
     * @return Set of widgets z-indexes in current search index suitable for current filter.
     */
    private Set<Integer> getSearchIndexSetOfZIndexes(TreeMap<Integer, Set<Integer>> searchIndex, int filter, boolean gte) {
        NavigableMap<Integer, Set<Integer>> map = gte
                ? searchIndex.tailMap(filter, true)
                : searchIndex.headMap(filter, true);
        return map
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Allow to get filtered widgets from repository by page.
     *
     * @param page   Page of widgets you want to get. Min value is 0.
     * @param size   Number of widgets in page. Min value is 1.
     * @param filter Object with filter properties.
     * @return List of filtered widgets on page.
     */
    @Override
    public PageHelperWrapper<WidgetCustomEntity> getFilteredEntities(int page, int size, WidgetFilterDTO filter) {
        Set<Integer> x1Match = getSearchIndexSetOfZIndexes(x1CoordinateSearchIndex, filter.getX1(), true);
        Set<Integer> y1Match = getSearchIndexSetOfZIndexes(y1CoordinateSearchIndex, filter.getY1(), true);
        Set<Integer> x2Match = getSearchIndexSetOfZIndexes(x2CoordinateSearchIndex, filter.getX2(), false);
        Set<Integer> y2Match = getSearchIndexSetOfZIndexes(y2CoordinateSearchIndex, filter.getY2(), false);

        x1Match.retainAll(y1Match);
        x1Match.retainAll(x2Match);
        x1Match.retainAll(y2Match);

        List<WidgetCustomEntity> widgets = x1Match
                .stream()
                .sorted()
                .skip(page * size)
                .limit(size)
                .map(widgetsStorage::get)
                .collect(Collectors.toList());

        return new PageHelperWrapper<>(widgets, x1Match.size());
    }
}
