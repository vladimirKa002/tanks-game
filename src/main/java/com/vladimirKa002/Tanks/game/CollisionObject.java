package com.vladimirKa002.Tanks.game;

import java.awt.geom.Area;

public abstract class CollisionObject extends GameObject{

    public CollisionObject(double[] position, double rotation) {
        super(position, rotation);
    }

    protected Area area;

    public Area getShape() {
        if (area == null) area = updatedShape();
        return area;
    }

    protected abstract Area updatedShape();
}
