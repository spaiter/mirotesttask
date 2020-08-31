package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.dto.ServiceLayerDTO;
import com.miro.api.widgets.testtask.utils.PageHelperWrapper;

import java.util.List;
import java.util.Optional;

/**
 * Common CRUD interface for any entity repository.
 * @param <Entity> Any entity for CRUD operations.
 */
public interface EntityRepository<Entity, CreateDTO extends ServiceLayerDTO, FilterDTO extends ServiceLayerDTO> {
    /**
     * Allow to create Entity from object with all necessary params.
     * @param createDTO Object with all params necessary to create Entity.
     * @return Entity
     */
    Entity createEntity(CreateDTO createDTO);

    /**
     * Allow to search entity by its ID in repository.
     * @param id Entity unique ID.
     * @return Entity by unique id if exists.
     */
    Optional<Entity> findEntityById(String id);

    /**
     * Allow to get all entities from repository.
     * @return List of all entities.
     */
    List<Entity> findAllEntities();

    /**
     * Allow to get entities from repository by page.
     * @param page Page of entities you want to get. Min value is 0.
     * @param size Number of entities in page. Min value is 1.
     * @return List of entities on page.
     */
    PageHelperWrapper<Entity> findAllEntities(int page, int size);

    /**
     * Allow to save (upsert) entity in repository.
     * @param entity Any entity to save.
     * @return just saved entity.
     */
    Entity saveEntity(Entity entity);


    /**
     * Allow to remove entity by its ID from repository.
     * @param id Entity unique ID.
     * @return True if entity was found and removed successfully, else false.
     */
    boolean deleteEntityById(String id);


    /**
     * Allow to remove all entities from repository.
     */
    void purge();


    /**
     * Allow to get all entities count.
     * @return count of all entities.
     */
    int getCount();

    /**
     * Allow to get filtered entities from repository by page.
     * @param page Page of entities you want to get. Min value is 0.
     * @param size Number of entities in page. Min value is 1.
     * @param filter Object with filter properties.
     * @return List of filtered entities on page.
     */
    PageHelperWrapper<Entity> getFilteredEntities(int page, int size, FilterDTO filter);
}
