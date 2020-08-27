package com.miro.api.widgets.testtask.dto;

public class WidgetUpdateDTO implements DTO {
    private final int xCoordinate;

    private final int yCoordinate;

    private final int zIndex;

    private final int height;

    private final int width;

    public WidgetUpdateDTO(WidgetUpdateRequestDTO requestDTO) {
        this.xCoordinate = requestDTO.getXCoordinate();
        this.yCoordinate = requestDTO.getYCoordinate();
        this.zIndex = requestDTO.getZIndex();
        this.height = requestDTO.getHeight();
        this.width = requestDTO.getWidth();
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
