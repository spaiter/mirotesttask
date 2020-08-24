package com.miro.api.widgets.testtask.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Common CRUD interface for any entity.
 * @param <Entity> Any entity for CRUD operations.
 */
public interface EntityRepository<Entity> {
    Optional<Entity> findEntityById(String id);
    List<Entity> findAllEntities();
    void saveEntity(Entity entity);
    boolean deleteEntityById(String id);
}
