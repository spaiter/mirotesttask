package com.miro.api.widgets.testtask.entities;

public abstract class AbstractWidgetEntity implements WidgetEntity {
    @Override
    public String toString() {
        return "WidgetEntity{" +
                "id='" + getId() + '\'' +
                ", xCoordinate=" + getXCoordinate() +
                ", yCoordinate=" + getYCoordinate() +
                ", zIndex=" + getZIndex() +
                ", height=" + getHeight() +
                ", width=" + getWidth() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
