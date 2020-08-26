package com.miro.api.widgets.testtask.entities;

/**
 * Class that contains all necessary params to create widget entity.
 */
public class WidgetUpdateParamsHelperEntity implements Entity {
    private final int xCoordinate;
    private final int yCoordinate;
    private final int zIndex;
    private final int height;
    private final int width;

    public WidgetUpdateParamsHelperEntity(int xCoordinate, int yCoordinate, int zIndex, int height, int width) {
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

    public int getZIndex() {
        return zIndex;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
