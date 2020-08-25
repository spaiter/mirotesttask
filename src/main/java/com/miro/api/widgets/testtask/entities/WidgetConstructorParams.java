package com.miro.api.widgets.testtask.entities;

/**
 * Class that contains all necessary params to create widget entity.
 */
public class WidgetConstructorParams {
    private final int xCoordinate;
    private final int yCoordinate;
    private Integer zIndex;
    private final int height;
    private final int width;

    public WidgetConstructorParams(int xCoordinate, int yCoordinate, Integer zIndex, int height, int width) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zIndex = zIndex;
        this.height = height;
        this.width = width;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
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

    public int getWidth() {
        return width;
    }
}
