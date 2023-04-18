package com.vladimirKa002.Tanks.game.Effects;

public class AudioEffect extends Effect {

    public AudioEffect(String name){
        super(name);
    }

    @Override
    public String toString() {
        return "{\"name\": \"" + name + "\"}";
    }
}
