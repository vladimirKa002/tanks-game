package com.vladimirKa002.Tanks.game;

public class AudioEffect {
    private final String name;

    public AudioEffect(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return "{\"name\": \"" + name + "\"}";
    }
}
