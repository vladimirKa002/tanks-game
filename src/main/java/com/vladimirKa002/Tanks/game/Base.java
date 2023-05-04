package com.vladimirKa002.Tanks.game;

import com.vladimirKa002.Tanks.game.Static.StaticCircleObject;

import java.awt.geom.Area;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Base extends StaticCircleObject {
    private final Game game;
    private final BaseConfig baseConfig;

    public Base(double[] position, BaseConfig baseConfig, Game game) {
        super(position, 0, baseConfig.collisionRadius, baseConfig.radius, null);
        this.baseConfig = baseConfig;
        this.game = game;
    }

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
        // So, total amount of scores added if many tanks on the base is SCORE_PER_TICK * MANY_TANKS
        //   TODO : check
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
            // TODO : check score punishment
            if (score == 0) p = 0;
            else p = p - SCORE_PUNISHMENT * p / score;
            if (p < 0) p = 0;
            tanksContribution.put(tank, p);
        }
    }

    @Override
    public String toString() {
        int progress = (int) (score / baseConfig.getScore() * 100);
        return "{" +
                "\"progress\": " + (Math.min(progress, 100))  +
                ", \"position\": " + Arrays.toString(position) +
                ", \"radius\": " + baseConfig.radius +
                ", \"team\": \"" + team + "\"" +
                '}';
    }

    public double getScore() {
        return score;
    }

    public String getCaptorWinner() {
        if (score >= baseConfig.getScore()) {
            return team;
        }
        return null;
    }

    public enum BaseConfig{
        SMALL(50, 10), // Score = time
        MEDIUM(60, 20),
        LARGE(75, 30);

        private final double radius;
        private final double collisionRadius;
        private final int score;
        private static final double COLLISION_RADIUS_MULTIPLIER = 0.7;

        BaseConfig(double radius, int score) {
            this.radius = radius;
            this.collisionRadius = radius * COLLISION_RADIUS_MULTIPLIER;
            this.score = score;
        }

        public double getRadius() {
            return radius;
        }

        public double getCollisionRadius() {
            return collisionRadius;
        }

        public int getScore() { return score; }
    }
}
