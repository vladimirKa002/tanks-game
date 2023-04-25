package com.vladimirKa002.Tanks.game.Static;

import com.vladimirKa002.Tanks.game.RectangleObject;

import java.util.Arrays;

public class StaticRectangleObject extends RectangleObject implements Static{
    private final double[] shape; // Image size (can be slightly larger than collision size)
    protected final String graphics;

    public StaticRectangleObject(double[] position, double rotation, double[] collisionShape,
                                 double[] shape, String graphics) {
        super(position, rotation, collisionShape);
        this.shape = shape;
        this.graphics = graphics;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\": \"rectangle\", " +
                "\"graphics\": \"" + graphics + "\", " +
                "\"position\": " + Arrays.toString(position) + ", " +
                "\"rotation\": " + rotation + ", " +
                "\"shape\": " + Arrays.toString(shape) +
                '}';
    }

    public String getGraphics(){
        return graphics;
    }
}
