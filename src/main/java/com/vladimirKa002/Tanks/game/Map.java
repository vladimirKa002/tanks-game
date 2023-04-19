package com.vladimirKa002.Tanks.game;

import java.awt.geom.Area;
import java.util.*;

public class Map {
    public static final int DEFAULT_UNITS = 500;
    public static final int DEFAULT_GAME_TIME = 60;

    private final int units;
    private final String type;
    private final HashSet<String> graphics;
    private final ArrayList<CollisionObject> obstacles;
    private final Area area;
    private final double[] basePosition;
    private final String backSoundName;
    private final int time;

    // Prebaked data
    private final String mapString;
    private final String obstaclesString;

    public final List<List<double[]>> tanksPositions_teams = new ArrayList<>(2);

    public Map(
            int units,
            String type,
            HashSet<String> graphics,
            ArrayList<CollisionObject> obstacles,
            Area area,
            double[] basePosition,
            String backSoundName,
            int time){
        this.units = units;
        this.type = type;
        this.graphics = graphics;
        this.area = area;
        this.basePosition = basePosition;
        this.backSoundName = backSoundName;
        this.obstacles = obstacles;
        this.time = time;

        mapString = "{" +
                "\"units\": " + units +
                ", \"type\": \"" + type + '\"' +
                '}';
        obstaclesString = "{ \"obstacles\": " + obstacles + "}";

        generateTanksPositions();
    }

    private void generateTanksPositions(){
        double padding = 50;

        ArrayList<double[]> l = new ArrayList<>(3);
        l.add(new double[]{padding, padding}); l.add(new double[]{(double) units / 2, padding}); l.add(new double[]{units - padding, padding});
        tanksPositions_teams.add(l);
        ArrayList<double[]> l2 = new ArrayList<>(3);
        l2.add(new double[]{padding, units - padding}); l2.add(new double[]{(double) units / 2, units - padding}); l2.add(new double[]{units - padding, units - padding});
        tanksPositions_teams.add(l2);
    }

    public List<double[]> getTanksPositions_teamOne() {
        List<double[]> newList = new ArrayList<>(3);
        for (double[] ds : tanksPositions_teams.get(0))
            newList.add(ds.clone());
        return newList;
    }

    public List<double[]> getTanksPositions_teamTwo() {
        List<double[]> newList = new ArrayList<>(3);
        for (double[] ds : tanksPositions_teams.get(1))
            newList.add(ds.clone());
        return newList;
    }

    public String getType(){
        return type;
    }

    public HashSet<String> getGraphics(){
        return graphics;
    }

    public int getUnits(){
        return units;
    }

    public List<CollisionObject> getObstacles() {
        return obstacles;
    }

    public Area getArea(){
        return area;
    }

    public String getBackSound(){
        return backSoundName;
    }

    public int getTime(){
        return time;
    }

    @Override
    public String toString() {
        return mapString;
    }

    public String getMapString(){
        return obstaclesString;
    }

    public double[] getBasePosition() {
        return basePosition;
    }
}
