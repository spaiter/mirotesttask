package com.miro.api.widgets.testtask.entities;

import com.miro.api.widgets.testtask.dto.WidgetCreateDTO;

import java.time.Instant;
import java.util.UUID;

/**
 * Widget class, that contains all widget description.
 */

public class WidgetEntity implements Entity {
    private String id;
    private int xCoordinate;
    private int yCoordinate;
    private int zIndex;
    private int height;
    private int width;
    private long updatedAt;

    public WidgetEntity(int xCoordinate, int yCoordinate, int zIndex, int height, int width) {
        this.id = UUID.randomUUID().toString();
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
        this.updatedAt = Instant.now().getEpochSecond();
    }

    public WidgetEntity(WidgetCreateDTO createDTO) {
        this.id = UUID.randomUUID().toString();
        this.xCoordinate = createDTO.getXCoordinate();
        this.yCoordinate = createDTO.getYCoordinate();
        this.zIndex = createDTO.getZIndex();
        this.height = createDTO.getHeight();
        this.width = createDTO.getWidth();
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

    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public void markUpdated() {
        this.updatedAt = Instant.now().getEpochSecond();
    }

    @Override
    public String toString() {
        return "WidgetEntity{" +
                "id='" + id + '\'' +
                ", xCoordinate=" + xCoordinate +
                ", yCoordinate=" + yCoordinate +
                ", zIndex=" + zIndex +
                ", height=" + height +
                ", width=" + width +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
