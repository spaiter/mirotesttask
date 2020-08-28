package com.miro.api.widgets.testtask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Widget description that will be produced to client via API response.
 */
public class WidgetResponseDTO implements AppLayerDTO {
    private final String id;
    private final int xCoordinate;
    private final int yCoordinate;
    private final int zIndex;
    private final int height;
    private final int width;
    private final long updatedAt;

    public WidgetResponseDTO(String id, int xCoordinate, int yCoordinate, int zIndex, int height, int width, long updatedAt) {
        this.id = id;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    @JsonProperty("xCoordinate")
    public int getXCoordinate() {
        return xCoordinate;
    }

    @JsonProperty("yCoordinate")
    public int getYCoordinate() {
        return yCoordinate;
    }

    @JsonProperty("zIndex")
    public int getZIndex() {
        return zIndex;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }
}
