package com.vladimirKa002.Tanks.game;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.awt.geom.Area;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Field {
    private static final String[] FIELDS = {"forest", "city", "desert"};
    private final int units = 500;

    private final String type;

    private final HashSet<String> graphics = new HashSet<>(10);

    private final ArrayList<CollisionObject> obstacles = new ArrayList<>(50);
    private final Area area = new Area();

    public final List<List<double[]>> tanksPositions_teams = new ArrayList<>(2);

    private double[] basePosition;

    // Creating a field. Randomly pick one of types defined in FIELDS
    public Field(){
        Random rnd = new Random();
        type = FIELDS[rnd.nextInt(FIELDS.length)];

        double padding = 50;

        ArrayList<double[]> l = new ArrayList<>(3);
        l.add(new double[]{padding, padding}); l.add(new double[]{(double) units / 2, padding}); l.add(new double[]{units - padding, padding});
        tanksPositions_teams.add(l);
        ArrayList<double[]> l2 = new ArrayList<>(3);
        l2.add(new double[]{padding, units - padding}); l2.add(new double[]{(double) units / 2, units - padding}); l2.add(new double[]{units - padding, units - padding});
        tanksPositions_teams.add(l2);

        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:game/maps/" + type + ".json");
        try {
            JSONObject data = new JSONObject(resourceAsString(resource));
            JSONArray obstaclesData = data.getJSONArray("obstacles");
            for(int i = 0; i < obstaclesData.length(); i++) {
                JSONObject objects = obstaclesData.getJSONObject(i);
                if (objects.has("active")) {
                    continue;
                }
                double radius = 0;
                if (objects.has("radius")) {
                    radius = objects.getDouble("radius");
                }
                String graphic = null;
                if (objects.has("graphic")) {
                    graphic = objects.getString("graphic");
                }
                JSONArray jsonArray = (JSONArray) objects.get("position");
                double[] position = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};

                double[] shape = null;
                if (objects.has("shape")) {
                    jsonArray = (JSONArray) objects.get("shape");
                    shape = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};
                }

                double[] shapeItem = null;
                if (objects.has("shapeItem")) {
                    jsonArray = (JSONArray) objects.get("shapeItem");
                    shapeItem = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};
                }

                double rotation = objects.getDouble("rotation");
                if (objects.getString("name").equals("tree"))
                    obstacles.add(new Tree(position, graphic));
                else if (objects.getString("name").equals("hut"))
                    obstacles.add(new Hut(position, rotation));
                else if (objects.getString("name").equals("desert-rock"))
                    obstacles.add(new Rock(position, rotation, radius, graphic));
                else if (objects.getString("name").equals("car")) {
                    Car car = new Car(position, rotation);
                    obstacles.add(car);
                    graphic = car.getGraphics();
                }
                else if (objects.getString("name").equals("building")) {
                    Building building = new Building(position, rotation, shapeItem, shape,
                            "buildings/" + objects.getString("game"));
                    obstacles.add(building);
                    graphic = building.getGraphics();
                }
                graphics.add(graphic);
            }
            JSONArray jsonArray = data.getJSONArray("base");
            basePosition = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};
        } catch (JSONException e) {
            e.printStackTrace();
            basePosition = new double[]{(double) units / 2, (double) units / 2};
        }

        for (CollisionObject obstacle : obstacles) {
            area.add(obstacle.getShape());
        }
    }

    public List<List<double[]>> getTanksPositions_teams() {
        return tanksPositions_teams;
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

    @Override
    public String toString() {
        return "{" +
                "\"units\": " + units +
                ", \"type\": \"" + type + '\"' +
                '}';
    }

    public String getMap(){
        return "{ \"obstacles\": " + obstacles + "}";
    }

    public static String resourceAsString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public double[] getBasePosition() {
        return basePosition;
    }
}
