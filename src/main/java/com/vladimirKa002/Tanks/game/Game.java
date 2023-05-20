package com.vladimirKa002.Tanks.game;

import com.vladimirKa002.Tanks.GameResources;
import com.vladimirKa002.Tanks.GameResources.TankPosition;
import com.vladimirKa002.Tanks.PageController;
import com.vladimirKa002.Tanks.TanksApplication;
import com.vladimirKa002.Tanks.game.Effects.AudioEffect;
import com.vladimirKa002.Tanks.game.Effects.Effect;
import com.vladimirKa002.Tanks.game.Effects.VisualEffect;

import java.util.*;
import java.util.stream.Collectors;

public class Game implements Runnable{
    public final String id;

    public static HashMap<String, Game> games = new HashMap<>();

    private final Tank[] tanks;
    private final HashMap<String, Tank> tanksMap = new HashMap<>();

    private String state = "not started";
    private final Map map;
    private final Base base;
    private final Timer timer;

    private final HashMap<String, List<Tank>> teams = new HashMap<>();


    /**
     * Creating tanks (without setting users) and assigning them to teams
     *
     * @param size    size of the map
     */
    public Game(Map.MapSize size) {
        id = TanksApplication.getId();
        games.put(id, this);
        map = GameResources.getInstance().getRandomMap(size);
        int playersNum = map.getPlayersAmount();

        base = new Base(map.getBasePosition(), Base.BaseConfig.valueOf(size.name().toUpperCase()), this);

        tanks = new Tank[playersNum];

        Random rnd = new Random();

        ArrayList<String> teamNames = new ArrayList<>(6);
        teamNames.add("&#128058;"); teamNames.add("&#128059;"); teamNames.add("&#128048;");
        teamNames.add("&#129418;"); teamNames.add("&#128047;"); teamNames.add("&#129409;");

        String team1 = teamNames.remove(rnd.nextInt(teamNames.size()));
        String team2 = teamNames.remove(rnd.nextInt(teamNames.size()));

        teams.put(team1, new ArrayList<>(playersNum / 2));
        teams.put(team2, new ArrayList<>(playersNum / 2));

        HashMap<String, List<TankPosition>> tanksPositions = new HashMap<>();
        tanksPositions.put(team1, map.getTanksPositions_teamOne());
        tanksPositions.put(team2, map.getTanksPositions_teamTwo());

        ArrayList<String> teamsN = new ArrayList<>(playersNum);
        for (int i = 0; i < playersNum / 2; i++) {
            teamsN.add(team1);
            teamsN.add(team2);
        }

        for (int i = 0; i < playersNum; i++) {
            String tName = teamsN.remove(rnd.nextInt(teamsN.size()));
            TankPosition tankPosition = tanksPositions.get(tName).remove(
                    rnd.nextInt(tanksPositions.get(tName).size()));
            tanks[i] = new Tank(tankPosition.getPosition(), tankPosition.getRotation(), tName, this);
            teams.get(tName).add(tanks[i]);
        }

        int time = playersNum / 2 * 60;
        timer = new Timer(time, FPS);
    }

    /**
     * Setting user to tank
     *
     * @param user  session id
     */
    public void setTankUser(String user){
        for (Tank tank: tanks) {
            if (tank.getUser() != null && tank.getUser().equals(user)) return;
        }
        for (Tank tank: tanks) {
            if (tank.getUser() == null) {
                tank.setUser(user);
                tanksMap.put(tank.getUser(), tank);
                return;
            }
        }
    }

    /**
     * Freeing the tank if user disconnected while waiting
     *
     * @param user  session id
     */
    public void removeTankUser(String user){
        tanksMap.get(user).setUser(null);
        tanksMap.remove(user);
    }

    public int tanksReady(){
        return tanksMap.keySet().size();
    }

    public void startGame(){
        state = "active";

        Thread thread = new Thread(this);
        thread.start();
    }

    public void finishGame(){
        state = "finished";
    }

    private final ArrayList<Projectile> projectiles = new ArrayList<>(5);
    private final ArrayList<VisualEffect> visualEffects = new ArrayList<>(10);
    private final ArrayList<AudioEffect> audioEffects = new ArrayList<>(10);

    public synchronized void updateProjectiles(){
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);
            projectile.update();
            if (!projectile.isActive()) {
                visualEffects.add(new VisualEffect(projectile.position, projectile.rotation, "hit", Effect.EFFECT_TO_ALL));
                projectiles.remove(i);
                i--;
            }
        }
    }

    public synchronized void addProjectile(Projectile projectile){
        projectiles.add(projectile);
    }

    public synchronized void updateEffects(){
        audioEffects.clear();
        for (int i = 0; i < visualEffects.size(); i++) {
            VisualEffect visualEffect = visualEffects.get(i);
            visualEffect.update();
            if (!visualEffect.isActive()) {
                visualEffects.remove(i);
                i--;
            }
        }
    }

    public synchronized void addVisualEffect(VisualEffect visualEffect){
        visualEffects.add(visualEffect);
    }

    public synchronized void addAudioEffect(AudioEffect audioEffect){
        this.audioEffects.add(audioEffect);
    }

    /**
     * Getting the state of a game
     *
     * @return  json format of a game
     */
    public synchronized String getState(){
        return "{\"tanks\": " + Arrays.toString(tanks) + ", " +
                "\"time\": \"" + timer.getTime() + "\", " +
                "\"state\": \"" + state + "\", " +
                "\"winner\": \"" + winner + "\", " +
                "\"base\": " + base.toString() + ", " +
                "\"projectiles\": [" + projectiles.stream().map(Object::toString)
                .collect(Collectors.joining(", ")) + "] " + ", " +
                "\"audioEffects\": [" + audioEffects.stream().map(Object::toString)
                .collect(Collectors.joining(", ")) + "] " + ", " +
                "\"visualEffects\": [" + visualEffects.stream().map(Object::toString)
                    .collect(Collectors.joining(", ")) + "] " +
                "}";
    }

    public static final int FPS = 60;

    private static final int LAST_TICKS = 100;

    private String winner = null;

    /**
     * The game loop. Checks winner, updates objects
     */
    @Override
    public void run() {
        int last_ticks = 0;
        timer.start();

        try {
            while (!state.equals("finished") || last_ticks < LAST_TICKS) {
                try {
                    if (state.equals("finished")) last_ticks++;

                    if (!state.equals("finished")) base.update();
                    updateEffects();
                    if (!state.equals("finished")) updateProjectiles();
                    if (!state.equals("finished")) x: {
                        String captor = base.getCaptorWinner();
                        if (captor != null) {
                            winner = captor;
                            finishGame();
                            break x;
                        }
                        for (Tank tank : tanks) {
                            if (tank.getHealth() == 0) continue;
                            tank.update();
                        }
                        ArrayList<String> aliveTeams = new ArrayList<>(2);
                        for (String s : teams.keySet()) {
                            List<Tank> tanks = teams.get(s);
                            int alive = 0;
                            for (Tank tank : tanks) {
                                if (tank.getHealth() > 0) {
                                    alive++;
                                }
                            }
                            if (alive > 0) aliveTeams.add(s);
                        }
                        if (aliveTeams.size() == 1) {
                            base.update();
                            winner = aliveTeams.get(0);
                            finishGame();
                            break x;
                        }
                        // Updating the timer
                        timer.update();
                        if (timer.finished()) {
                            finishGame();
                        }
                    }
                    Thread.sleep(1000/FPS);
                }
                catch (InterruptedException ignored) {
                }
            }
        }
        catch (Exception ignored){
            games.remove(id);
        }
    }

    /**
     * Performing actions
     *
     * @param dataPackage   defines actions from user to be done
     */
    public synchronized void performActions(PageController.DataPackage dataPackage){
        Tank tank = tanksMap.get(dataPackage.session_id);

        if (tank == null) return;

        double fpsMultiplier = dataPackage.elapsed / (1000.0 / FPS);

        for (String action: dataPackage.actions.split(";")) {
            switch (action) {
                case "forward":
                    tank.move(1, fpsMultiplier);
                    break;
                case "backward":
                    tank.move(-1, fpsMultiplier);
                    break;
                case "right":
                    tank.rotateTank(1, fpsMultiplier);
                    break;
                case "left":
                    tank.rotateTank(-1, fpsMultiplier);
                    break;
                case "head_right":
                    tank.rotateHead(1);
                    break;
                case "shot":
                    tank.shot();
                    break;
                case "head_left":
                    tank.rotateHead(-1);
                    break;
            }
        }
    }

    public String getPlayState(){
        return state;
    }

    public Tank[] getTanks(){
        return tanks;
    }

    public int getTanksAmount(){
        return tanks.length;
    }

    public Tank getTank(String user){
        return tanksMap.get(user);
    }

    public Map getMap(){
        return map;
    }

    public Base getBase() {
        return base;
    }
}
