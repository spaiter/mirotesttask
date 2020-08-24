package com.miro.api.widgets.testtask.entities;

import java.time.Instant;
import java.util.UUID;

/**
 * Widget class, that contains all widget description.
 */
public class WidgetEntity {
    public String id;
    public CoordinatePointEntity coordinatePoint;
    public Integer zIndex;
    public Integer height;
    public Integer width;
    public Instant updatedAt;

    public WidgetEntity(Integer xCoordinate, Integer yCoordinate, Integer zIndex, Integer height, Integer width) {
        this.id = UUID.randomUUID().toString();
        this.coordinatePoint = new CoordinatePointEntity(xCoordinate, yCoordinate);
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
        this.updatedAt = Instant.now();
    }
}
