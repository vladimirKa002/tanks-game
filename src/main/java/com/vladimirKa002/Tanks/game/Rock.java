package com.vladimirKa002.Tanks.game;

public class Rock extends StaticCircleObject{
    public Rock(double[] position, double rotation, double radius, String graphics) {
        super(new double[]{position[0], position[1]}, rotation,
                radius, radius, graphics, "tree");
    }
}
