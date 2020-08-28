package com.miro.api.widgets.testtask.dto;

public class WidgetCreateDTO implements ServiceLayerDTO {
    private final int xCoordinate;

    private final int yCoordinate;

    private Integer zIndex;

    private final int height;

    private final int width;

    public WidgetCreateDTO(WidgetCreateRequestDTO requestDTO) {
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

    public Integer getZIndex() {
        return zIndex;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setZIndex(int index) {
        this.zIndex = index;
    }
}
