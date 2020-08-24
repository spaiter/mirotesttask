package com.miro.api.widgets.testtask.entities;

/**
 * Point, that represents position on Cartesian coordinate system.
 */
public class CoordinatePointEntity {
    private Integer x;
    private Integer y;

    public CoordinatePointEntity(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
