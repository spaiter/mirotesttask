package com.miro.api.widgets.testtask.entities;

import java.time.Instant;

public interface WidgetEntity extends CommonEntity {
    String getId();

    void setId(String id);

    int getXCoordinate();

    void setXCoordinate(int xCoordinate);

    int getYCoordinate();

    void setYCoordinate(int yCoordinate);

    int getZIndex();

    void setZIndex(int zIndex);

    int getHeight();

    void setHeight(int height);

    int getWidth();

    void setWidth(int width);

    long getUpdatedAt();

    void setUpdatedAt(long updatedAt);

    default void markUpdated() {
        setUpdatedAt(Instant.now().getEpochSecond());
    }
}
