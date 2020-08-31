package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.dto.ServiceLayerDTO;
import com.miro.api.widgets.testtask.entities.WidgetEntity;

/**
 * Common CRUD interface for any entity repository, where entity could be shifted by unique int index.
 * @param <Entity> Any entity for CRUD operations and shifting.
 */
public interface ShiftableIntIndexEntityRepository<Entity, CreateDTO extends ServiceLayerDTO, FilterDTO extends ServiceLayerDTO> extends EntityRepository<Entity, CreateDTO, FilterDTO> {
    /**
     * Allow to get max entity unique int index.
     * @return max entity index.
     */
    int getMaxIndex();

    /**
     * Check that entities should be shifted by int index. Use on create only.
     * @param index Entity unique int index.
     * @return true if shifting is necessary, else false.
     */
    boolean isNeedToShift(int index);

    /**
     * Check that entities should be shifted by int index. Use on update only.
     * @param index Entity unique int index.
     * @param id Entity id.
     * @return true if shifting is necessary, else false.
     */
    boolean isNeedToShift(int index, String id);

    /**
     * Shift entities by int index/
     * @param index Entity unique int index.
     */
    void shiftUpwards(int index);
}
