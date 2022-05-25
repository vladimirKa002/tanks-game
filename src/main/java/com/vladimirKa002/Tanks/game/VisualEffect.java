package com.vladimirKa002.Tanks.game;

import java.util.Arrays;

public class VisualEffect extends GameObject {
    private final String name;
    private boolean isActive = true;
    private double[] shape;

    public VisualEffect(double[] position, double rotation, String name){
        super(position, rotation);
        this.name = name;

        if (name.equals("hit")) shape = new double[]{0.3 * sizeMultiplier, 0.3 * sizeMultiplier};
        else if (name.equals("gun_shot")) shape = new double[]{0.3 * sizeMultiplier, 0.566 * sizeMultiplier};
    }

    public boolean isActive() {
        return isActive;
    }

    private int progress = 0; // percents
    private static final double EFFECT_DURATION = 0.25; //seconds
    private double effectTime = 0;

    @Override
    public void update() {
        if (progress != 100) {
            effectTime += 1.0 / (double) Game.FPS;
            progress = (int) (effectTime / EFFECT_DURATION * 100);
            if (progress >= 100) {
                isActive = false;
            }
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"graphics\": \"" + name + "\", " +
                "\"rotation\": " + rotation + ", " +
                "\"shape\": " + Arrays.toString(shape) + ", " +
                "\"position\": " + Arrays.toString(position) +
                '}';
    }
}
