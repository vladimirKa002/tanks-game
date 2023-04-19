package com.vladimirKa002.Tanks.game;

import java.util.Arrays;

public class StaticRectangleObject extends RectangleObject implements Static{
    private final double[] shape; // Image size (can be slightly larger than collision size)
    protected final String graphics;
    private final String name;

    public StaticRectangleObject(double[] position, double rotation, double[] collisionShape,
                                 double[] shape, String graphics, String name) {
        super(position, rotation, collisionShape);
        this.shape = shape;
        this.graphics = graphics;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + name + "\", " +
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
