package com.miro.api.widgets.testtask.entities;

/**
 * Point, that represents position on Cartesian coordinate system.
 */
public class CoordinatePointEntity {
    private int x;
    private int y;

    public CoordinatePointEntity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
