package com.steve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Maze {

    public enum TILEROLE{
        ENTRANCE,
        WAYPOINTS,
        DEADEND,
        EXIT,
        EMPTY
    }

    private int width;
    private int length;
    private int waypoints;
    private int deadends;
    private int entrances;
    private int exits;

    private ArrayList<ArrayList<TILEROLE>> generatedMaze;

    public Maze(int width, int length, int waypoints, int deadends,int entrances, int exits){
        this.width = width;
        this.length = length;
        this.waypoints = waypoints;
        this.deadends = deadends;
        this.entrances = entrances;

        generatedMaze = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            ArrayList<TILEROLE> list = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                list.add(TILEROLE.EMPTY);
            }

            generatedMaze.add(list);
        }
    }

    public void generateMaze(){
        ArrayList<TILEROLE> list = generatedMaze.get(0);
        for(int i = 0; i < entrances;i++){
            int randomInt = ThreadLocalRandom.current().nextInt(1,width);
            if(list.get(randomInt) != TILEROLE.ENTRANCE){
                list.set(randomInt, TILEROLE.ENTRANCE);
            }else{
                list.set((randomInt+(width-1)/2)%width-1, TILEROLE.ENTRANCE);
            }
        }
    }

    public ArrayList<ArrayList<TILEROLE>> getMaze(){
        return generatedMaze;
    }
}
