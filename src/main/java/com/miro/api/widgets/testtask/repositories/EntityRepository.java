package com.miro.api.widgets.testtask.repositories;

import java.util.List;

public interface EntityRepository<Entity> {
    Entity findEntityById(String id);
    List<Entity> findAllEntities();
    Void saveEntity(Entity entity);
    Boolean deleteEntityById(String id);
}
