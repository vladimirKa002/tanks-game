package com.vladimirKa002.Tanks.game.Effects;

import com.vladimirKa002.Tanks.GameResources;
import com.vladimirKa002.Tanks.game.Game;

import java.util.Arrays;

public class VisualEffect extends Effect {
    public static final double DEFAULT_VISUAL_EFFECT_DURATION = 0.25; // seconds

    private boolean isActive = true;
    private final double[] shape;
    private final double duration;

    public VisualEffect(double[] position, double rotation, String name, String toWhom){
        super(position, rotation, name, toWhom);

        VisualEffectInfo info = GameResources.getInstance().getVisualEffectInfo(name);

        this.duration = info.getDuration();
        if (info.shape[0] == Double.MAX_VALUE) this.shape = new double[]{1000, 1000};
        else this.shape = new double[]{info.shape[0] * sizeMultiplier, info.shape[1] * sizeMultiplier};
    }

    public boolean isActive() {
        return isActive;
    }

    protected int progress = 0; // percents
    protected double effectTime = 0;

    @Override
    public void update() {
        if (progress != 100) {
            effectTime += 1.0 / (double) Game.FPS;
            progress = (int) (effectTime / duration * 100);
            if (progress >= 100) {
                isActive = false;
            }
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"graphics\": \"" + name + "\", " +
                "\"toWhom\": \"" + toWhom + "\", " +
                "\"rotation\": " + rotation + ", " +
                "\"shape\": " + Arrays.toString(shape) + ", " +
                "\"position\": " + Arrays.toString(position) +
                '}';
    }

    public static class VisualEffectInfo{
        private final double[] shape;
        private final double duration;

        public VisualEffectInfo(double[] shape, double duration) {
            this.shape = shape;
            this.duration = duration;
        }

        public double[] getShape() {
            return shape;
        }

        public double getDuration() {
            return duration;
        }
    }
}
