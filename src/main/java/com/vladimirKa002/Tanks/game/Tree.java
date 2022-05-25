package com.vladimirKa002.Tanks.game;

import java.util.Random;

public class Tree extends StaticCircleObject{
    private static final double collisionRadius = 0.25 * sizeMultiplier;
    private static final double radius = 0.3 * sizeMultiplier; // radius of graphics

    public Tree(double[] position, String graphics) {
        super(new double[]{position[0], position[1]}, new Random().nextInt(360),
                collisionRadius, radius, graphics, "tree");
    }
}
