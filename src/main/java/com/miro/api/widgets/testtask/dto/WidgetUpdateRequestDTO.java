package com.miro.api.widgets.testtask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
