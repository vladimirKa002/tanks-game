package com.vladimirKa002.Tanks.game;

import com.vladimirKa002.Tanks.GameResources.TankPosition;

import java.awt.geom.Area;
import java.util.*;

public class Map {
    private static final Random rnd = new Random();
    public static final int DEFAULT_UNITS = 500;

    private final int units;
    private final int[] playersAmount;
    private final String name;
    private final String size;
    private final HashSet<String> graphics;
    private final ArrayList<CollisionObject> obstacles;
    private final Area area;
    private final double[] basePosition;
    private final String backSoundName;

    // Prebaked data
    private final String mapString;
    private final String obstaclesString;

    public final List<List<TankPosition>> tanksPositions_teams;

    public Map(
            int units,
            int[] playersAmount,
            String name,
            String size,
            HashSet<String> graphics,
            ArrayList<CollisionObject> obstacles,
            List<List<TankPosition>> tanksPositions_teams,
            Area area,
            double[] basePosition,
            String backSoundName){
        this.units = units;
        this.playersAmount = playersAmount;
        this.name = name;
        this.size = size;
        this.graphics = graphics;
        this.area = area;
        this.basePosition = basePosition;
        this.backSoundName = backSoundName;
        this.obstacles = obstacles;
        this.tanksPositions_teams = tanksPositions_teams;

        mapString = "{" +
                "\"units\": " + units +
                ", \"name\": \"" + name + '\"' +
                '}';
        obstaclesString = "{ \"obstacles\": " + obstacles + "}";
    }

    public List<TankPosition> getTanksPositions_teamOne() {
        List<TankPosition> newList = new ArrayList<>(3);
        newList.addAll(tanksPositions_teams.get(0));
        return newList;
    }

    public List<TankPosition> getTanksPositions_teamTwo() {
        List<TankPosition> newList = new ArrayList<>(3);
        newList.addAll(tanksPositions_teams.get(1));
        return newList;
    }

    public int getPlayersAmount(){
        int min = playersAmount[0];
        int max = playersAmount[1];
        return (min + rnd.nextInt(max - min + 1)) * 2;
    }

    public String getName(){
        return name;
    }

    public String getSize(){
        return size;
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
