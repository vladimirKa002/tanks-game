package com.vladimirKa002.Tanks.game;

import java.util.Random;

public class Car extends StaticRectangleObject{
    private static final String[] graphics = {"cars/car_white", "cars/car_red", "cars/car_black"
            , "cars/car_blue", "cars/car_grey"};

    public Car(double[] position, double rotation) {
        super(position, rotation,
                new double[]{0.26 * sizeMultiplier, 0.67 * sizeMultiplier},
                new double[]{0.35 * sizeMultiplier, 0.72 * sizeMultiplier}, // image size
                graphics[new Random()
                .nextInt(graphics.length)], "car");
    }
}
