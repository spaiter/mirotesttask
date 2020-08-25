package com.miro.api.widgets.testtask.entities;

import java.time.Instant;
import java.util.UUID;

/**
 * Widget class, that contains all widget description.
 */
public class WidgetEntity {
    private String id;
    private final CoordinatePointEntity coordinatePoint;
    private int zIndex;
    private int height;
    private int width;
    private long updatedAt;

    public WidgetEntity(int xCoordinate, int yCoordinate, int zIndex, int height, int width) {
        this.id = UUID.randomUUID().toString();
        this.coordinatePoint = new CoordinatePointEntity(xCoordinate, yCoordinate);
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
        this.updatedAt = Instant.now().getEpochSecond();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getXCoordinate() {
        return this.coordinatePoint.getX();
    }

    public void setXCoordinate(int xCoordinate) {
        this.coordinatePoint.setX(xCoordinate);
    }

    public int getYCoordinate() {
        return this.coordinatePoint.getY();
    }

    public void setYCoordinate(int yCoordinate) {
        this.coordinatePoint.setY(yCoordinate);
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "WidgetEntity{" +
                "id='" + id + '\'' +
                ", coordinatePoint=" + coordinatePoint +
                ", zIndex=" + zIndex +
                ", height=" + height +
                ", width=" + width +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
