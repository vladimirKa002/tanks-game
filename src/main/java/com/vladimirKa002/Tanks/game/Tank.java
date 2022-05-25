package com.vladimirKa002.Tanks.game;

import java.awt.geom.Area;
import java.util.*;
import java.util.List;

public class Tank extends RectangleObject{
    private int health = 100;

    private static final double[] bodyShape = {0.499 * sizeMultiplier, 1 * sizeMultiplier};
    private static final double[] headShape = {0.499 * sizeMultiplier, 1.143 * sizeMultiplier};

    private static final double[] headPosition = {0 * sizeMultiplier, -0.303 * sizeMultiplier};
    private static final double[] headRotationPos = {0 * sizeMultiplier, 0 * sizeMultiplier};

    private static final double FORWARD_MOVE = 1; // Forward move speed
    private static final double BACKWARD_MOVE = -0.75; // Backward move speed
    private static final double ROTATION = 1.25; // Rotation move speed

    private double head_rotation = 0;
    private static final double HEAD_ROTATION = 1; // Head rotation speed
    private static final double RELOADING_TIME = 3; // seconds

    private final String graphic;

    private double[] prevPosition;
    private double prevRotation;

    private String user = null;

    public String getTeam() {
        return team;
    }

    private final String team;

    public Tank(double[] position, String fieldType, String team, int units){
        super(position, (position[1] < (double) units / 2 ? 180 : 0), bodyShape);

        this.team = team;

        switch (fieldType) {
            case "forest":
                graphic = "green";
                break;
            case "desert":
                graphic = "sand";
                break;
            case "winter":
                graphic = "white";
                break;
            case "city":
                graphic = "grey";
                break;
            default:
                graphic = "green";
                break;
        }
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void move(int direction){
        double x_dir = -Math.sin(-Math.toRadians(rotation));
        double y_dir = -Math.cos(-Math.toRadians(rotation));

        prevPosition = position.clone();

        if (direction == 1){
            position[0] += x_dir * FORWARD_MOVE;
            position[1] += y_dir * FORWARD_MOVE;
        }
        else if (direction == -1){
            position[0] += x_dir * BACKWARD_MOVE;
            position[1] += y_dir * BACKWARD_MOVE;
        }
    }

    public void rotateTank(int direction){
        prevRotation = rotation;

        if (direction == 1) {
            rotation += ROTATION;
        }
        else if (direction == -1) {
            rotation -= ROTATION;
        }
        rotation = (rotation + 360) % 360;
    }

    public void revertMovement(){
        position[0] = prevPosition[0];
        position[1] = prevPosition[1];
    }

    public void revertRotation(){
        rotation = prevRotation;
    }

    // In case if any collision happened, revert previous action
    public void checkCollisions(List<CollisionObject> objects, Area object, String mode){
        Area area = getShape();
        area.intersect(object);
        if (!area.isEmpty()) {
            if (mode.equals("rot")) revertRotation();
            else {
                revertMovement();
            }
        }

        for (CollisionObject obj: objects){
            if (obj.id == this.id) continue;
            area = getShape();
            area.intersect(obj.getShape());
            if (!area.isEmpty()) {
                if (mode.equals("rot")) revertRotation();
                else {
                    revertMovement();
                }
            }
        }
    }

    public void checkPosition(int units){
        if (position[0] < 0 || position[1] < 0 || position[0] > units || position[1] > units) {
            revertMovement();
        }
    }

    public void setHealth(int damage){
        health -= damage;
        if (health < 0) health = 0;
    }

    public int getHealth(){
        return health;
    }

    public double getTankRotation(){
        return rotation;
    }

    public void rotateHead(int direction){
        if (direction == 1) {
            head_rotation += HEAD_ROTATION;
        }
        else if (direction == -1) {
            head_rotation -= HEAD_ROTATION;
        }
        head_rotation = (head_rotation + 360) % 360;
    }

    public double getHeadRotation(){
        return head_rotation;
    }

    private int reloadingProgress = 100; // percents
    private double reloadingTime = RELOADING_TIME;

    public void shot(Game game){
        if (reloadingProgress != 100) return;

        double angle = rotation + head_rotation;
        double[] shotPosition = position.clone();

        double pos = headPosition[1] - headShape[1] / 2 - 0.25 * sizeMultiplier;

        double x_dir = Math.sin(-Math.toRadians(angle));
        double y_dir = Math.cos(-Math.toRadians(angle));
        shotPosition[0] += x_dir * pos;
        shotPosition[1] += y_dir * pos;

        game.addProjectile(new Projectile(position.clone(), angle, this, game));
        game.addEffect(new VisualEffect(shotPosition.clone(), angle, "gun_shot"));

        reloadingProgress = 0;
        reloadingTime = 0;
    }

    @Override
    public void update(){
        if (reloadingProgress != 100) {
            reloadingTime += 1.0 / (double) Game.FPS;
            reloadingProgress = (int) (reloadingTime / RELOADING_TIME * 100);
            if (reloadingProgress >= 100) {
                reloadingProgress = 100;
                reloadingTime = RELOADING_TIME;
            }
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ",\"team\": \"" + team + '\"' +
                ", \"health\": " + health +
                ", \"headRotation\": " + head_rotation +
                ", \"user\": \"" + user + '\"' +
                ", \"rotation\": " + rotation +
                ", \"position\": " + Arrays.toString(position) +
                ", \"headShape\": " + Arrays.toString(headShape) +
                ", \"bodyShape\": " + Arrays.toString(bodyShape) +
                ", \"headPosition\": " + Arrays.toString(headPosition) +
                ", \"headRotationPos\": " + Arrays.toString(headRotationPos) +
                ", \"headRotationSpeed\": " + HEAD_ROTATION +
                ", \"reloading\": " + reloadingProgress +
                '}';
    }

    public String getGraphic() {
        return graphic;
    }
}
