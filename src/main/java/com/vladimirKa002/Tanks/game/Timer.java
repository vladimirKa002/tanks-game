package com.vladimirKa002.Tanks.game;

public class Timer extends GameObject{
    private final int fps;
    private final int time;
    private boolean started = false;

    private double _timeSeconds = 0;
    private int _time = 0;

    public Timer(int time, int fps){
        this.time = time;
        this.fps = fps;
    }

    @Override
    public void update(){
        if (!started) return;
        _timeSeconds += (1.0 / fps);
        _time = time - (int) Math.ceil(_timeSeconds);
        if (_time < 0) _time = 0;
    }

    public void start(){
        started = true;
    }

    public int getTime(){
        return _time;
    }

    public boolean finished(){
        return _time <= 0;
    }
}
