package com.vladimirKa002.Tanks.game;

import com.vladimirKa002.Tanks.game.Static.Static;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public abstract class RectangleObject extends CollisionObject{

    protected final double[] collisionShape;

    public RectangleObject(double[] position, double rotation, double[] collisionShape) {
        super(position, rotation);
        this.collisionShape = collisionShape;
    }

    @Override
    protected Area updatedShape() {
        double x = position[0] - collisionShape[0] / 2;
        double y = position[1] - collisionShape[1] / 2;
        double w = collisionShape[0];
        double h = collisionShape[1];

        Rectangle2D.Double rect = new Rectangle2D.Double(x, y, w, h);

        AffineTransform af = new AffineTransform();
        af.rotate(Math.toRadians(rotation), position[0], position[1]);

        return new Area(rect).createTransformedArea(af);
    }
}
