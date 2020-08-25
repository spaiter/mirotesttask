package com.miro.api.widgets.testtask.repositories;

/**
 * Common CRUD interface for any entity repository, where entity could be shifted by unique int index.
 * @param <Entity> Any entity for CRUD operations and shifting.
 */
public interface ShiftableIntIndexEntityRepository<Entity> extends EntityRepository<Entity> {
    /**
     * Allow to get max entity unique int index.
     * @return max entity index.
     */
    int getMaxIndex();

    /**
     * Check that entities should be shifted by int index.
     * @param index Entity unique int index.
     * @return true if shifting is necessary, else false.
     */
    boolean isNeedToShift(int index);

    /**
     * Shift entities by int index/
     * @param index Entity unique int index.
     */
    void shiftUpwards(int index);
}
