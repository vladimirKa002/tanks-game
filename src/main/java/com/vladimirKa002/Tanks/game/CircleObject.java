package com.vladimirKa002.Tanks.game;

import com.vladimirKa002.Tanks.game.Static.Static;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public abstract class CircleObject extends CollisionObject{
    protected final double collisionRadius;

    public CircleObject(double[] position, double rotation, double collisionRadius) {
        super(position, rotation);
        this.collisionRadius = collisionRadius;
    }

    @Override
    public Area updatedShape() {
        double x = position[0] - collisionRadius;
        double y = position[1]  - collisionRadius;
        double w = collisionRadius * 2;
        double h = collisionRadius * 2;

        Ellipse2D.Double circle = new Ellipse2D.Double(x, y, w, h);

        return new Area(circle);
    }
}
