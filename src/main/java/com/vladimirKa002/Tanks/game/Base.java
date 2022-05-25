package com.vladimirKa002.Tanks.game;

import java.awt.geom.Area;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Base extends StaticCircleObject{
    private final Game game;
    private static final int COLLISION_RADIUS = 35;
    private static final int RADIUS = 50;

    public Base(double[] position, Game game) {
        super(position, 0, COLLISION_RADIUS, RADIUS, null, "base");
        this.game = game;
    }

    private static final int MAX_SCORE = 10;
    private static final double SCORE_PUNISHMENT = 2.5;
    private static final double MANY_TANKS = 1.75;
    private static final double SCORE_PER_TICK = 0.016667;
    private double score = 0;
    private String team = null;
    private final HashMap<Tank, Double> tanksContribution = new HashMap<>();

    @Override
    public synchronized void update() {
        HashSet<String> teams = setTanksAndGetTeams();
        if (teams.size() > 1) {
            if (team != null) {
                double score = 0;
                for (Tank tank: tanksContribution.keySet()) {
                    if (tank.getTeam().equals(team)) score += tanksContribution.get(tank);
                }
                this.score = score;
            }
            return;
        }
        else if (teams.size() == 0) {
            team = null;
            score = 0;
            return;
        }

        double score = 0;
        double addScore = SCORE_PER_TICK;
        if (tanksContribution.keySet().size() >= 2)
            addScore = SCORE_PER_TICK * MANY_TANKS / tanksContribution.keySet().size();
        for (Tank tank: tanksContribution.keySet()) {
            tanksContribution.put(tank, tanksContribution.get(tank) + addScore);
            score += tanksContribution.get(tank);
        }
        this.score = score;
        team = (String) teams.toArray()[0];
    }

    /**
     * Update tanks on the base
     *
     * @return  a HashSet that contains unique team names
     */
    private synchronized HashSet<String> setTanksAndGetTeams(){
        HashSet<String> set = new HashSet<>(2);
        for (Tank tank: game.getTanks()) {
            if (tank.getHealth() <= 0) {
                tanksContribution.remove(tank);
                continue;
            }

            Area area = (Area) getShape().clone();
            area.intersect(tank.getShape());
            if (area.isEmpty()) {
                tanksContribution.remove(tank);
            }
            else {
                set.add(tank.getTeam());
                if (!tanksContribution.containsKey(tank)) tanksContribution.put(tank, 0.0);
            }
        }
        return set;
    }

    /**
     * Make a punishment for the base captors if one of tanks was damaged.
     *
     * @param tank1     check if damaged tank is on the base
     */
    public synchronized void resetPoints(Tank tank1){
        if (!tanksContribution.containsKey(tank1) ||
                (team != null && !tank1.getTeam().equals(team))) return;
        for (Tank tank : tanksContribution.keySet()) {
            double p = tanksContribution.get(tank);
            if (score == 0) p = 0;
            else p = p - SCORE_PUNISHMENT * p / score;
            if (p < 0) p = 0;
            tanksContribution.put(tank, p);
        }
    }

    @Override
    public String toString() {
        int progress = (int) (score / MAX_SCORE * 100);
        return "{" +
                "\"progress\": " + (Math.min(progress, 100))  +
                ", \"position\": " + Arrays.toString(position) +
                ", \"radius\": " + RADIUS +
                ", \"team\": \"" + team + "\"" +
                '}';
    }

    public double getScore() {
        return score;
    }

    public String getCaptorWinner() {
        if (score >= MAX_SCORE) {
            return team;
        }
        return null;
    }
}
