package com.vladimirKa002.Tanks.game.Effects;

import com.vladimirKa002.Tanks.game.GameObject;

public abstract class Effect extends GameObject {
    public static final String EFFECT_TO_ALL = "all";

    protected final String name;
    protected final String toWhom;

    public Effect(double[] position, double rotation, String name, String toWhom){
        super(position, rotation);
        this.name = name;
        this.toWhom = toWhom;
    }

    public Effect(String name, String toWhom){
        this.name = name;
        this.toWhom = toWhom;
    }

    @Override
    public abstract String toString();
}
