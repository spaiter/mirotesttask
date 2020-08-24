package com.miro.api.widgets.testtask.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Common CRUD interface for any entity repository.
 * @param <Entity> Any entity for CRUD operations.
 */
public interface EntityRepository<Entity> {
    /**
     * Allow to search entity by its ID in repository.
     * @param id Entity unique ID.
     * @return Entity by unique id if exists
     */
    Optional<Entity> findEntityById(String id);

    /**
     * Allow to get all entities from repository.
     * @return List of all entities.
     */
    List<Entity> findAllEntities();

    /**
     * Allow to save (upsert) entity in repository.
     * @param entity Any entity to save.
     */
    void saveEntity(Entity entity);


    /**
     * Allow to remove entity by its ID from repository.
     * @param id Entity unique ID.
     * @return True if entity was found and removed successfully, else false.
     */
    boolean deleteEntityById(String id);
}
