package com.vladimirKa002.Tanks;

import com.vladimirKa002.Tanks.game.Game;

import com.vladimirKa002.Tanks.game.Tank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@Controller
public class PageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping(value = {"/game"})
    public String getPageGame(Model model){
        return "game";
    }

    @RequestMapping(value = {"/main", "/"})
    public String getPageMain(Model model){
        return "main";
    }

    /**
     * Upon start of the game, user obtains graphics
     *
     * @param room_id       game id
     * @return              hashmap with graphics
     */
    @GetMapping("/game/init")
    public ResponseEntity<?> initGame(@RequestParam("room_id") Optional<String> room_id)
            throws Exception {
        Game game = Game.games.get(room_id.get());
        if (game == null) return ResponseEntity.ok(new ResponseMessage("GameNotFound", "The game was not found!"));
        return ResponseEntity.ok(new ResponseGameInit(
                getImages(game), getAudios(game), game.getState(), game.getMap().getMapString()));
    }

    private final HashMap<String, String> resourcesStorage = new HashMap<>(50);

    private HashMap<String, String> getImages(Game game) throws IOException {
        HashMap<String, String> images = new HashMap<>();

        addResource(images, "game\\graphics\\" + game.getMap().getType() + "-map.png", "map");

        addResource(images, "game\\graphics\\victory.png", "victory");
        addResource(images, "game\\graphics\\defeat.png", "defeat");
        addResource(images, "game\\graphics\\gun_shot.png", "hit");
        addResource(images, "game\\graphics\\damage.png", "damage");
        addResource(images, "game\\graphics\\gun_shot2.png", "gun_shot");
        addResource(images, "game\\graphics\\tanks\\destroyed_head.png", "destroyed_head");
        addResource(images, "game\\graphics\\tanks\\destroyed_body.png", "destroyed_body");

        for (String graphic : game.getMap().getGraphics()) {
            String gr = graphic.replaceAll("//", "\\");
            addResource(images, "game\\graphics\\" + gr + ".png", graphic);
        }

        for (Tank tank : game.getTanks()) {
            addResource(images, "game\\graphics\\tanks\\" + tank.getGraphic() + "\\head.png",
                    tank.id + "_head");
            addResource(images, "game\\graphics\\tanks\\" + tank.getGraphic() + "\\body.png",
                    tank.id + "_body");
        }
        return images;
    }

    private HashMap<String, String> getAudios(Game game) throws IOException {
        HashMap<String, String> audios = new HashMap<>();
        addResource(audios, "game\\audio\\shot.mp3", "shot");
        addResource(audios, "game\\audio\\air-alert.mp3", "air-alert");
        addResource(audios, "game\\audio\\background\\" + game.getMap().getBackSound() + ".mp3", "back-sound");
        return audios;
    }

    private void addResource(HashMap<String, String> images, String path, String name) throws IOException {
        if (resourcesStorage.containsKey(path)) {
            images.put(name, resourcesStorage.get(path));
            return;
        }

        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:" + path);

        byte[] buffer = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String result = Base64.getEncoder().encodeToString(buffer);
        images.put(name, result);
        resourcesStorage.put(path, result);
    }

    /**
     * When user wants to join a game, a new game object is created and he starts waiting
     *      or  he joins existing game. Player is assigned to one of tanks of a game.
     *
     * @param session_id_param  if session id is not defined, create new one
     * @param game_mode_param   duel or 2x2
     * @return                  data package which tells game id, session id (user id),
     *                              and if needed to wait
     */
    @GetMapping("/game/enter")
    public ResponseEntity<?> enterGame(@RequestParam("session_id") Optional<String> session_id_param,
                                       @RequestParam("game_mode") String game_mode_param) {
        String session_id = session_id_param.orElseGet(TanksApplication::getId);

        int playerNum;
        if (game_mode_param.equals("2x2")) playerNum = 4;
        else playerNum = 2;

        /*if (true) {
            Game game = new Game(playerNum);
            game.setTankUser(session_id);
            game.startGame();
            return ResponseEntity.ok(new ResponseSession(session_id, game.id, false, 0, 0));
        }*/

        for (Game value : Game.games.values()) {
            if (value.getTanks().length != playerNum) continue;
            if (value.getPlayState().equals("not started")) {
                value.setTankUser(session_id);
                int num = value.tanksReady();
                if (num == value.getTanks().length) {
                    value.startGame();
                    simpMessagingTemplate.convertAndSend("/waiting." + value.id,
                            new ResponseSession(session_id, value.id, false, num, playerNum));
                    return ResponseEntity.ok(new ResponseSession(session_id, value.id, false, num, playerNum));
                }
                simpMessagingTemplate.convertAndSend("/waiting." + value.id, new ResponseSession(session_id,
                        value.id, true, num, playerNum));
                return ResponseEntity.ok(new ResponseSession(session_id, value.id, true, num, playerNum));
            }
        }
        Game game = new Game(playerNum);
        game.setTankUser(session_id);
        return ResponseEntity.ok(new ResponseSession(session_id, game.id, true, 1, playerNum));
    }

    @GetMapping({"/waiting/page"})
    public String getPageWaiting(Model model,
                                 @RequestParam("game_mode") Optional<String> game_mode_param){
        model.addAttribute("game_mode", game_mode_param.orElse("Duel"));
        return "waiting";
    }

    @RequestMapping(value = {"/game/exit"})
    public ResponseEntity<?> exitGame(@RequestParam("session_id") String session_id){
        Game g = null;
        for (Game game : Game.games.values()) {
            if (game.getTank(session_id) != null) {
                g = game;
            }
        }
        if (g == null) return ResponseEntity.ok("OK");
        g.removeTankUser(session_id);
        simpMessagingTemplate.convertAndSend("/waiting." + g.id, new ResponseSession(
                session_id, g.id, true, g.tanksReady(), g.getTanks().length));
        return ResponseEntity.ok("OK");
    }

    // Saving previous state of a game
    private String prevState;

    /**
     * Update function for a game. Its being invoked whenever a request from user comes (in general,
     * every game tick). It can contain actions from the user to be performed.
     *
     * @param dataPackage       describes which actions were performed
     */
    @MessageMapping("/game/update")
    public void updateGame(DataPackage dataPackage) {
        Game game = Game.games.get(dataPackage.room_id);
        if (game == null) {
            System.out.println("The game was not found!");
            return;
        }

        game.performActions(dataPackage);

        String state = game.getState();
        if (prevState == null || !prevState.equals(state)) {
            simpMessagingTemplate.convertAndSend("/game." + dataPackage.room_id, state);
        }
        prevState = state;
    }

    public static class DataPackage {
        public String room_id;
        public String session_id;
        public String actions;

        public DataPackage(String room_id, String session_id, String actions) {
            this.room_id = room_id;
            this.session_id = session_id;
            this.actions = actions;
        }
    }

    static class ResponseSession{
        public String session_id;
        public String room_id;
        public boolean wait;
        public int num;
        public int ready;

        public ResponseSession(String session_id, String room_id, boolean wait,
                               int playersReady, int playerNum){
            this.room_id = room_id;
            this.session_id = session_id;
            this.wait = wait;
            this.ready = playersReady;
            this.num = playerNum;
        }
    }

    static class ResponseGameInit{
        public HashMap<String, String> images;
        public HashMap<String, String> audios;
        public String gameState;
        public String gameMap;

        public ResponseGameInit(HashMap<String, String> images, HashMap<String, String> audios, String gameState, String gameMap){
            this.images = images;
            this.audios = audios;
            this.gameState = gameState;
            this.gameMap = gameMap;
        }
    }

    static class ResponseMessage{
        public String message;
        public String code;

        public ResponseMessage(String code, String message){
            this.message = message;
            this.code = code;
        }
    }
}
