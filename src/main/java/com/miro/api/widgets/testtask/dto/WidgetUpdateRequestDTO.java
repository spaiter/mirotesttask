package com.miro.api.widgets.testtask.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class WidgetUpdateRequestDTO implements DTO {
    @NotNull
    private final int xCoordinate;

    @NotNull
    private final int yCoordinate;

    @NotNull
    private final int zIndex;

    @NotNull
    @Positive
    private final int height;

    @NotNull
    @Positive
    private final int width;

    public WidgetUpdateRequestDTO(int xCoordinate, int yCoordinate, int zIndex, int height, int width) {
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
