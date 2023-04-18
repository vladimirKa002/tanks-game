package com.vladimirKa002.Tanks;

import com.vladimirKa002.Tanks.game.*;
import com.vladimirKa002.Tanks.game.Map;
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

public class GameResources {
    private static GameResources gameResources;

    private final List<Map> maps = new ArrayList<>(MAPS.length);
    private final HashMap<String, double[]> visualEffects = new HashMap<>();

    private GameResources(){
        loadMaps();
        loadVisualEffects();
    }

    public static GameResources getInstance(){
        if (gameResources == null) gameResources = new GameResources();
        return gameResources;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Methods for loading game maps
    ///////////////////////////////////////////////////////////////////////////////////

    private static final String[] MAPS = {"forest", "city", "desert"};

    private void loadMaps(){
        for (String type: MAPS){
            ArrayList<CollisionObject> obstacles = new ArrayList<>(50);
            HashSet<String> graphics = new HashSet<>(10);
            Area area = new Area();
            String backSoundName = null;
            double[] basePosition;
            int units = 500;

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
                                "buildings/" + objects.getString("graphics"));
                        obstacles.add(building);
                        graphic = building.getGraphics();
                    }
                    graphics.add(graphic);
                }
                units = data.getInt("units");
                JSONArray jsonArray = data.getJSONArray("base");
                basePosition = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};
                backSoundName = data.getString("back-sound");
            } catch (JSONException e) {
                e.printStackTrace();
                basePosition = new double[]{(double) units / 2, (double) units / 2};
            }

            for (CollisionObject obstacle : obstacles) {
                area.add(obstacle.getShape());
            }

            maps.add(new Map(units, type, graphics, obstacles, area, basePosition, backSoundName));
        }
    }

    public Map getRandomMap(){
        Random rnd = new Random();
        return maps.get(rnd.nextInt(MAPS.length));
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Methods for loading Visual effects
    ///////////////////////////////////////////////////////////////////////////////////

    private void loadVisualEffects(){
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:game/graphics/visual-effects.json");
        try {
            JSONObject data = new JSONObject(resourceAsString(resource));
            Iterator iterator = data.keys();
            while (iterator.hasNext()){
                String name = (String) iterator.next();
                JSONObject object = data.getJSONObject(name);
                try{
                    String sh = object.optString("shape");
                    double[] shape;
                    if (sh.equals("full")) shape = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
                    else {
                        JSONArray jsonArray = (JSONArray) object.get("shape");
                        shape = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};
                    }
                    visualEffects.put(name, shape);
                }
                catch (JSONException e){
                    e.printStackTrace();
                    System.out.println("ERROR: Cannot parse visual effect `" + name + "`.");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double[] getVisualEffectSize(String name){
        return visualEffects.get(name);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Utility methods
    ///////////////////////////////////////////////////////////////////////////////////

    private static String resourceAsString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
