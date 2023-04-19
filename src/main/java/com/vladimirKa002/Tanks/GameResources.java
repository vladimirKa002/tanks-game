package com.vladimirKa002.Tanks;

import com.vladimirKa002.Tanks.game.*;
import com.vladimirKa002.Tanks.game.Effects.VisualEffect;
import com.vladimirKa002.Tanks.game.Map;
import com.vladimirKa002.Tanks.game.Static.StaticCircleObject;
import com.vladimirKa002.Tanks.game.Static.StaticRectangleObject;
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
    private final HashMap<String, VisualEffect.VisualEffectInfo> visualEffects = new HashMap<>();

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
        Random rnd = new Random();

        for (String type: MAPS){
            ArrayList<CollisionObject> obstacles = new ArrayList<>(50);
            HashSet<String> graphics = new HashSet<>(10);
            Area area = new Area();
            String backSoundName = null;
            double[] basePosition;
            int units = Map.DEFAULT_UNITS;
            int time = Map.DEFAULT_GAME_TIME;

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
                    String graphic = objects.getString("graphic");

                    JSONArray jsonArray = (JSONArray) objects.get("position");
                    double[] position = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};

                    // Setting size options
                    double[] shape = null;
                    double[] shapeCollision = null;
                    if (objects.has("shape")) {
                        jsonArray = (JSONArray) objects.get("shape");
                        shape = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};

                        jsonArray = objects.optJSONArray("shapeCollision");
                        if (jsonArray == null) shapeCollision = shape.clone();
                        else shapeCollision = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};
                    }
                    double radius = 0;
                    double radiusCollision = 0;
                    if (objects.has("radius")) {
                        radius = objects.getDouble("radius");
                        radiusCollision = objects.optDouble("radiusCollision", radius);
                    }

                    String name = objects.getString("name");
                    String _type = objects.getString("type");
                    double rotation = objects.optDouble("rotation", rnd.nextInt(360));
                    if (_type.equals("circle"))
                        obstacles.add(new StaticCircleObject(position, rotation, radiusCollision, radius, graphic, name));
                    else if (_type.equals("rectangle"))
                        obstacles.add(new StaticRectangleObject(position, rotation, shapeCollision, shape, graphic, name));
                    graphics.add(graphic);
                }
                units = data.optInt("units", units);
                time = data.optInt("time", time);
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

            maps.add(new Map(units, type, graphics, obstacles, area, basePosition, backSoundName, time));
        }
    }

    private HashMap<String, List<String>> getRandomGraphics(JSONObject rndGraphics) throws JSONException {
        HashMap<String, List<String>> randomGraphics = new HashMap<>();
        if (rndGraphics == null) return randomGraphics;
        Iterator iterator = rndGraphics.keys();
        while (iterator.hasNext()){
            String name = (String) iterator.next();
            JSONArray graphs = rndGraphics.getJSONArray(name);
            List<String> graphics = new ArrayList<>(graphs.length());
            for (int i = 0; i < graphs.length(); i++)
                graphics.add(graphs.getString(i));
            randomGraphics.put(name, graphics);
        }
        return randomGraphics;
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
                    double duration = object.optDouble("duration", VisualEffect.DEFAULT_VISUAL_EFFECT_DURATION);
                    visualEffects.put(name, new VisualEffect.VisualEffectInfo(shape, duration));
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

    public VisualEffect.VisualEffectInfo getVisualEffectInfo(String name){
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
