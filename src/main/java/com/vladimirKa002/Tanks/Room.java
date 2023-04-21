package com.vladimirKa002.Tanks;

import java.util.HashMap;

public class Room {
    private static final HashMap<String, Room> rooms = new HashMap<>();
    private final String id;

    private int players = 0;

    public Room(String id){
        this.id = id;
        rooms.put(id, this);
    }

    public String getId(){
        return id;
    }

    public void addPlayer(){
        players++;
    }

    public int getPlayers(){
        return players;
    }

    public static Room getRoom(String id){
        return rooms.getOrDefault(id, new Room(id));
    }
}
