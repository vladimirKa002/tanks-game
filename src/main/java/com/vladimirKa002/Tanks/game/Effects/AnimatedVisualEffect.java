package com.vladimirKa002.Tanks.game.Effects;

import com.vladimirKa002.Tanks.game.Game;

import java.util.Arrays;

public class AnimatedVisualEffect extends VisualEffect {
    public AnimatedVisualEffect(double[] position, double rotation, String name, String toWhom) {
        super(position, rotation, name, toWhom);
    }

    /*@Override
    public void update() {
        if (progress != 100) {
            effectTime += 1.0 / (double) Game.FPS;
            progress = (int) (effectTime / EFFECT_DURATION * 100);
            if (progress >= 100) {
                isActive = false;
            }
        }
    }*/

   /* @Override
    public String toString() {
        return "{" +
                "\"graphics\": \"" + name + "\", " +
                "\"toWhom\": \"" + toWhom + "\", " +
                "\"rotation\": " + rotation + ", " +
                "\"shape\": " + Arrays.toString(shape) + ", " +
                "\"position\": " + Arrays.toString(position) +
                '}';
    }*/
}
