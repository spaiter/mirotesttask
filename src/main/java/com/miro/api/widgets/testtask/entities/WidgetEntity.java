package com.miro.api.widgets.testtask.entities;

import java.time.Instant;
import java.util.UUID;

/**
 * Widget class, that contains all widget description.
 */
public class WidgetEntity {
    private String id;
    private final CoordinatePointEntity coordinatePoint;
    private Integer zIndex;
    private Integer height;
    private Integer width;
    private Instant updatedAt;

    public WidgetEntity(Integer xCoordinate, Integer yCoordinate, Integer zIndex, Integer height, Integer width) {
        this.id = UUID.randomUUID().toString();
        this.coordinatePoint = new CoordinatePointEntity(xCoordinate, yCoordinate);
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
        this.updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getXCoordinate() {
        return this.coordinatePoint.getX();
    }

    public void setXCoordinate(Integer xCoordinate) {
        this.coordinatePoint.setX(xCoordinate);
    }

    public Integer getYCoordinate() {
        return this.coordinatePoint.getY();
    }

    public void setYCoordinate(Integer yCoordinate) {
        this.coordinatePoint.setY(yCoordinate);
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
