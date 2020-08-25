package com.miro.api.widgets.testtask.entities;

import java.time.Instant;
import java.util.UUID;

/**
 * Widget class, that contains all widget description.
 */
public class WidgetEntity {
    private String id;
    private int xCoordinate;
    private int yCoordinate;
    private Integer zIndex;
    private int height;
    private int width;
    private long updatedAt;

    public WidgetEntity(int xCoordinate, int yCoordinate, Integer zIndex, int height, int width) {
        this.id = UUID.randomUUID().toString();
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
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
        return this.xCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getYCoordinate() {
        return this.yCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public void setZIndex(Integer zIndex) {
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
                ", xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                ", zIndex=" + zIndex +
                ", height=" + height +
                ", width=" + width +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
