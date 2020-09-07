package com.miro.api.widgets.testtask.repositories;

import com.miro.api.widgets.testtask.entities.WidgetJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqlWidgetEntityRepository extends PagingAndSortingRepository<WidgetJpaEntity, String>, ShiftableIntIndexEntityRepository {

    @Query("Select coalesce(max(w.zIndex), 0) from widget w")
    int getMaxIndex();

    @Query("Select w from widget w order by w.zIndex asc")
    Iterable<WidgetJpaEntity> findAll();

    @Query("Select w from widget w order by w.zIndex asc")
    Page<WidgetJpaEntity> findAll(Pageable pageable);

    @Query("Select w from widget w where w.x1Coordinate >= ?1 and w.y1Coordinate >= ?2 and w.x2Coordinate <= ?3  and w.y2Coordinate <= ?4 order by w.zIndex asc")
    Page<WidgetJpaEntity> getFilteredEntities(
            int x1,
            int y1,
            int x2,
            int y2,
            Pageable pageRequest
    );

    @Modifying(clearAutomatically = true)
    @Query("Update widget w set w.zIndex = w.zIndex + 1 where w.zIndex >= ?1")
    void shiftUpwards(int zIndex);

    @Query("Select count(w) > 0 from widget w where w.zIndex = ?1")
    boolean isNeedToShift(int index);

    @Query("Select count(w) > 0 from widget w where w.zIndex = ?1 and w.id = ?2")
    boolean isNeedToShift(int index, String id);
}
