package com.miro.api.widgets.testtask.dto;

/**
 * Widget description that will be produced to client via API response.
 */
public class WidgetResponseDTO {
    public String id;
    public Integer xCoordinate;
    public Integer yCoordinate;
    public Integer zIndex;
    public Integer height;
    public Integer width;
    public Integer updatedAt;

    public WidgetResponseDTO(String id, Integer xCoordinate, Integer yCoordinate, Integer zIndex, Integer height, Integer width, Integer updatedAt) {
        this.id = id;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
        this.updatedAt = updatedAt;
    }
}
