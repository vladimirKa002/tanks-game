package com.vladimirKa002.Tanks.game;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameObject {
    protected static final double sizeMultiplier = 65;

    protected static final AtomicInteger counter = new AtomicInteger(0);
    public final int id;

    protected double[] position;
    protected double rotation;

    public GameObject(double[] position, double rotation){
        this.position = position;
        this.rotation = rotation;
        this.id = counter.incrementAndGet();
    }

    public GameObject(){
        this.id = counter.incrementAndGet();
    }

    public void update(){

    }

    public double[] getPosition() {
        return position;
    }

    public double getRotation() {
        return rotation;
    }
}
