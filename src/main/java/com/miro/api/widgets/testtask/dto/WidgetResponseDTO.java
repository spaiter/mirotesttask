package com.miro.api.widgets.testtask.dto;

/**
 * Widget description that will be produced to client via API response.
 */
public class WidgetResponseDTO {
    private final String id;
    private final Integer xCoordinate;
    private final Integer yCoordinate;
    private final Integer zIndex;
    private final Integer height;
    private final Integer width;
    private final Integer updatedAt;

    public WidgetResponseDTO(String id, Integer xCoordinate, Integer yCoordinate, Integer zIndex, Integer height, Integer width, Integer updatedAt) {
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

    public Integer getXCoordinate() {
        return xCoordinate;
    }

    public Integer getYCoordinate() {
        return yCoordinate;
    }

    public Integer getZIndex() {
        return zIndex;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getUpdatedAt() {
        return updatedAt;
    }
}
