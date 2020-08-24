package com.miro.api.widgets.testtask.views;

/**
 * Widget description that will be produced to client in API response.
 */
public class WidgetResponseView {
    public String index;
    public Integer xCoordinate;
    public Integer yCoordinate;
    public Integer zIndex;
    public Integer height;
    public Integer width;
    public Integer updatedAt;

    public WidgetResponseView(String index, Integer xCoordinate, Integer yCoordinate, Integer zIndex, Integer height, Integer width, Integer updatedAt) {
        this.index = index;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
        this.updatedAt = updatedAt;
    }
}
