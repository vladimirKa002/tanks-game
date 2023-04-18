package com.vladimirKa002.Tanks.game.Effects;

public class AudioEffect extends Effect {

    public AudioEffect(String name, String toWhom){
        super(name, toWhom);
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\": \"" + name + "\", " +
                "\"toWhom\": \"" + toWhom + "\"" +
                "}";
    }
}
