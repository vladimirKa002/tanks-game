package com.vladimirKa002.Tanks.game.Effects;

import com.vladimirKa002.Tanks.game.GameObject;

public abstract class Effect extends GameObject {
    protected final String name;

    public Effect(double[] position, double rotation, String name){
        super(position, rotation);
        this.name = name;
    }

    public Effect(String name){
        this.name = name;
    }

    @Override
    public abstract String toString();
}
