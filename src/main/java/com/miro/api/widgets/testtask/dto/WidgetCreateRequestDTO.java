package com.miro.api.widgets.testtask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class WidgetCreateRequestDTO implements AppLayerDTO {
    @NotNull
    private final Integer xCoordinate;

    @NotNull
    private final Integer yCoordinate;

    private final Integer zIndex;

    @NotNull
    @Positive
    private final Integer height;

    @NotNull
    @Positive
    private final Integer width;

    public WidgetCreateRequestDTO(Integer xCoordinate, Integer yCoordinate, Integer zIndex, Integer height, Integer width) {
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
