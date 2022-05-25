package com.vladimirKa002.Tanks.game;

public class Building extends StaticRectangleObject{
    public Building(double[] position, double rotation,
                    double[] shapeItem,
                    double[] shape,
                    String graphics) {
        super(position, rotation, shapeItem, shape, graphics, "building");
    }
}
