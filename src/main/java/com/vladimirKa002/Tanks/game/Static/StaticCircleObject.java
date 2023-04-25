package com.vladimirKa002.Tanks.game.Static;

import com.vladimirKa002.Tanks.game.CircleObject;

import java.util.Arrays;

public class StaticCircleObject extends CircleObject implements Static{
    private final double radius; // Image radius (can be slightly larger than collision size)
    protected final String graphics;

    public StaticCircleObject(double[] position, double rotation, double collisionRadius,
                              double radius, String graphics) {
        super(position, rotation, collisionRadius);
        this.radius = radius;
        this.graphics = graphics;
    }

    @Override
    public String toString() {
        return "{" +
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
