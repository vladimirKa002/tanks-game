package com.vladimirKa002.Tanks.game;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Projectile extends RectangleObject {
    private static final double SPEED = 40;
    private static final int DAMAGE_MIN = 15; // Minimal damage

    private static final double[] shape = {2.6, 6.5};

    private final double[] initPos;

    private final Game game;
    private final Tank owner;

    private boolean isActive = true;

    public Projectile(double[] position, double rotation, Tank owner, Game game) {
        super(position, rotation, shape);
        initPos = position.clone();
        this.owner = owner;
        this.game = game;
    }

    private double hitDist = Double.MAX_VALUE;

    private final ArrayList<Hit> hits = new ArrayList<>(5);
    static class Hit{
        public double dist;
        public double[] pos;
        public CollisionObject obj;

        public Hit(double dist, double[] pos, CollisionObject obj) {
            this.dist = dist;
            this.pos = pos;
            this.obj = obj;
        }
    }

    /**
     * Checking intersection with object's area
     *
     * @param area  Object's area
     * @return      Returns true if intersects
     */
    private void checkIntersection(Area area, CollisionObject obj){
        // Creating a vector in a form of rectangle in a direction of a projectile
        Rectangle2D.Double vector = new Rectangle2D.Double(initPos[0] - shape[0], initPos[1],
                shape[0] * 2, Math.sqrt((position[0] - initPos[0]) * (position[0] - initPos[0]) +
                (position[1] - initPos[1]) * (position[1] - initPos[1])));

        AffineTransform af = new AffineTransform();
        af.rotate(Math.toRadians((rotation + 180) % 360), initPos[0], initPos[1]);

        Area vectorArea = new Area(vector).createTransformedArea(af);
        vectorArea.intersect(area);

        if (!vectorArea.isEmpty()) {
            // We get a bounding rectangle of the intersection
            // and get its corner that is the closest to initial point.
            // It should be precise enough to get hit position
            Rectangle2D.Double rect = (Rectangle2D.Double) vectorArea.getBounds2D();
            double[][] points = new double[4][2];
            points[0][0] = rect.x; points[0][1] = rect.y;
            points[1][0] = rect.x + rect.width; points[1][1] = rect.y;
            points[2][0] = rect.x; points[2][1] = rect.y + rect.height;
            points[3][0] = rect.x + rect.width; points[3][1] = rect.y + rect.height;

            double min_d = Double.MAX_VALUE;
            double[] pos = null;
            for (double[] point: points) {
                double dist = Math.sqrt((point[0] - initPos[0]) * (point[0] - initPos[0]) +
                        (point[1] - initPos[1]) * (point[1] - initPos[1]));
                if (dist < min_d) {
                    min_d = dist;
                    pos = new double[]{point[0], point[1]};
                }
            }
            hits.add(new Hit(min_d, pos, obj));
        }
    }

    private boolean checkPosition(int units){
        return position[0] < 0 || position[1] < 0 || position[0] > units || position[1] > units;
    }

    /**
     * Getting damage of a projectile. The closer the target to init point is
     * the more damage projectile will cause.
     *
     * @return  damage amount
     */
    public int getDamage(){
        Random rnd = new Random();

        int damage = DAMAGE_MIN + rnd.nextInt(6);

        int damageDist = 10;
        final int DAMAGE_DISTANCE = 250;
        if (hitDist < DAMAGE_DISTANCE) damage += damageDist;
        else {
            int addD = damageDist * (int) ((hitDist - DAMAGE_DISTANCE) / DAMAGE_DISTANCE);
            if (addD > 0) {
                if (addD > 15) damage += damageDist;
                else damage += addD;
            }
        }
        return damage;
    }

    public boolean isActive(){
        return isActive;
    }

    @Override
    public void update() {
        double x_dir = -Math.sin(-Math.toRadians(rotation));
        double y_dir = -Math.cos(-Math.toRadians(rotation));

        position[0] += x_dir * SPEED;
        position[1] += y_dir * SPEED;

        checkIntersection(game.getMap().getArea(), null);
        for (Tank tank: game.getTanks()) {
            if (tank.id != owner.id) {
                checkIntersection(tank.getShape(), tank);
            }
        }
        if (hits.size() != 0) {
            isActive = false;
            CollisionObject obj = null;

            for (Hit hit : hits) {
                if (hitDist > hit.dist) {
                    hitDist = hit.dist;
                    position = hit.pos;
                    obj = hit.obj;
                }
            }
            if (obj instanceof Tank){
                Tank tank = (Tank) obj;
                if (!tank.getTeam().equals(owner.getTeam())) {
                    game.getBase().resetPoints(tank);
                    tank.setDamage(getDamage());
                }
            }
        }

        if (checkPosition(game.getMap().getUnits())) {
            isActive = false;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"shape\": " + Arrays.toString(shape) + ", " +
                "\"rotation\": " + rotation + ", " +
                "\"position\": " + Arrays.toString(position) +
                '}';
    }
}
