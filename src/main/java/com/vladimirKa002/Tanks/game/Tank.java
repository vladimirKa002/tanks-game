package com.vladimirKa002.Tanks.game;

import com.vladimirKa002.Tanks.game.Effects.AudioEffect;
import com.vladimirKa002.Tanks.game.Effects.Effect;
import com.vladimirKa002.Tanks.game.Effects.VisualEffect;

import java.awt.geom.Area;
import java.util.*;

public class Tank extends RectangleObject{
    private int health = 100;

    private static final double[] bodyShape = {32.435, 65};
    private static final double[] headShape = {32.435, 74.295};

    private static final double[] headPosition = {0, -19.695};
    private static final double[] headRotationPos = {0, 0};

    private static final double FORWARD_MOVE = 1; // Forward move speed
    private static final double BACKWARD_MOVE = -0.75; // Backward move speed
    private static final double ROTATION = 1.25; // Rotation move speed

    private double head_rotation = 0;
    private static final double HEAD_ROTATION = 1; // Head rotation speed
    private static final double RELOADING_TIME = 3; // seconds

    private final String graphic;

    private final Game game;

    private String user = null;

    public String getTeam() {
        return team;
    }

    private final String team;

    public Tank(double[] position, double rotation, String team, Game game){
        super(position, rotation, bodyShape);

        graphic = game.getMap().getCamo();

        this.team = team;
        this.game = game;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void move(int direction, double multiplier){
        double[] prevPosition = position.clone();

        double x_dir = -Math.sin(-Math.toRadians(rotation));
        double y_dir = -Math.cos(-Math.toRadians(rotation));

        if (direction == 1){
            position[0] += x_dir * FORWARD_MOVE * multiplier;
            position[1] += y_dir * FORWARD_MOVE * multiplier;
        }
        else if (direction == -1){
            position[0] += x_dir * BACKWARD_MOVE * multiplier;
            position[1] += y_dir * BACKWARD_MOVE * multiplier;
        }

        Area tempArea = updatedShape();

        if (checkPosition() || checkCollisions(tempArea)) {
            position = prevPosition;
            return;
        }

        area = tempArea;
    }

    public void rotateTank(int direction, double multiplier){
        double prevRotation = rotation;

        if (direction == 1) {
            rotation += ROTATION * multiplier;
        }
        else if (direction == -1) {
            rotation -= ROTATION * multiplier;
        }
        rotation = (rotation + 360) % 360;

        Area tempArea = updatedShape();

        if (checkCollisions(tempArea)) {
            rotation = prevRotation;
            return;
        }

        area = tempArea;
    }

    // In case if any collision happened, revert previous action
    private boolean checkCollisions(Area area){
        Area _area = (Area) area.clone();
        _area.intersect(game.getMap().getArea());
        if (!_area.isEmpty()) {
            return true;
        }

        for (CollisionObject obj: game.getTanks()){
            if (obj.id == this.id) continue;
            _area = (Area) area.clone();
            _area.intersect(obj.getShape());
            if (!_area.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPosition(){
        return position[0] < 0 || position[1] < 0 ||
                position[0] > game.getMap().getUnits() || position[1] > game.getMap().getUnits();
    }

    public void setDamage(int damage){
        int pos = game.getMap().getUnits() / 2;
        game.addVisualEffect(new VisualEffect(new double[]{pos, pos}, 0, "damage", id + ""));
        health -= damage;
        if (health <= 0) {
            health = 0;
            // game.addVisualEffect(new VisualEffect(new double[]{pos, pos}, 0, "expl-tank", Effect.EFFECT_TO_ALL));
            // game.addAudioEffect(new AudioEffect("expl-tank", Effect.EFFECT_TO_ALL));
        }
    }

    public int getHealth(){
        return health;
    }

    public double getTankRotation(){
        return rotation;
    }

    public void rotateHead(int direction, double multiplier){
        if (direction == 1) {
            head_rotation += HEAD_ROTATION * multiplier;
        }
        else if (direction == -1) {
            head_rotation -= HEAD_ROTATION * multiplier;
        }
        head_rotation = (head_rotation + 360) % 360;
    }

    public double getHeadRotation(){
        return head_rotation;
    }

    private int reloadingProgress = 100; // percents
    private double reloadingTime = RELOADING_TIME;

    public void shot(){
        if (reloadingProgress != 100) return;

        double angle = rotation + head_rotation;
        double[] shotPosition = position.clone();

        double pos = headPosition[1] - headShape[1] / 2 - 16.25;

        double x_dir = Math.sin(-Math.toRadians(angle));
        double y_dir = Math.cos(-Math.toRadians(angle));
        shotPosition[0] += x_dir * pos;
        shotPosition[1] += y_dir * pos;

        game.addProjectile(new Projectile(position.clone(), angle, this, game));
        game.addVisualEffect(new VisualEffect(shotPosition.clone(), angle, "gun_shot", Effect.EFFECT_TO_ALL));
        game.addAudioEffect(new AudioEffect("shot", Effect.EFFECT_TO_ALL));

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
