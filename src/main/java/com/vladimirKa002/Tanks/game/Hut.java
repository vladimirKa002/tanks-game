package com.vladimirKa002.Tanks.game;

public class Hut extends StaticRectangleObject {
    public Hut(double[] position, double rotation) {
        super(position, rotation, new double[]{0.8 * sizeMultiplier, 0.9 * sizeMultiplier},
                new double[]{0.9 * sizeMultiplier, 1 * sizeMultiplier}, "hut", "hut");
        this.rotation = rotation;
    }
}
