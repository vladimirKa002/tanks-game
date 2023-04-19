package com.vladimirKa002.Tanks.game.Static;

import com.vladimirKa002.Tanks.game.CircleObject;

import java.util.Arrays;

public class StaticCircleObject extends CircleObject implements Static{
    private final double radius; // Image radius (can be slightly larger than collision size)
    protected final String graphics;
    private final String name;

    public StaticCircleObject(double[] position, double rotation, double collisionRadius,
                              double radius, String graphics, String name) {
        super(position, rotation, collisionRadius);
        this.radius = radius;
        this.graphics = graphics;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + name + "\", " +
                "\"type\": \"circle\", " +
                "\"graphics\": \"" + graphics + "\", " +
                "\"position\": " + Arrays.toString(position) + ", " +
                "\"rotation\": " + rotation + ", " +
                "\"radius\": " + radius +
                '}';
    }

    public String getGraphics(){
        return graphics;
    }
}
