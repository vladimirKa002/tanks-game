package com.vladimirKa002.Tanks.game.Effects;

import com.vladimirKa002.Tanks.GameResources;
import com.vladimirKa002.Tanks.game.Game;

import java.util.Arrays;

public class VisualEffect extends Effect {
    private boolean isActive = true;
    private final double[] shape;

    public VisualEffect(double[] position, double rotation, String name){
        super(position, rotation, name);

        double[] shape = GameResources.getInstance().getVisualEffectSize(name);
        if (shape[0] == Double.MAX_VALUE){
            this.shape = new double[]{1000 * sizeMultiplier, 1000 * sizeMultiplier};
        }
        else this.shape = new double[]{shape[0] * sizeMultiplier, shape[1] * sizeMultiplier};
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
