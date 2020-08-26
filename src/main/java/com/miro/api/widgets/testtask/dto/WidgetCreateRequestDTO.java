package com.miro.api.widgets.testtask.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class WidgetCreateRequestDTO implements DTO {
    @NotNull
    private final int xCoordinate;

    @NotNull
    private final int yCoordinate;

    private final Integer zIndex;

    @NotNull
    @Positive
    private final int height;

    @NotNull
    @Positive
    private final int width;

    public WidgetCreateRequestDTO(int xCoordinate, int yCoordinate, Integer zIndex, int height, int width) {
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
