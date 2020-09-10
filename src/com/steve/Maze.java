package com.steve;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Maze {

    public enum TILEROLE{
        ENTRANCE,
        WAYPOINT,
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
        this.exits = exits;

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

        //Set entrances
        ArrayList<TILEROLE> list = generatedMaze.get(0);
        for(int i = 0; i < entrances;i++){
            int randomInt = ThreadLocalRandom.current().nextInt(0,width);
            if(list.get(randomInt) != TILEROLE.ENTRANCE){
                list.set(randomInt, TILEROLE.ENTRANCE);
            }else{
                list.set((randomInt+(width-1)/2)%width-1, TILEROLE.ENTRANCE);
            }
        }

        //Set exits
        list = generatedMaze.get(width-1);
        for(int i = 0; i < exits; i++){
            int randomInt = ThreadLocalRandom.current().nextInt(0,width-1);
            if(list.get(randomInt) != TILEROLE.EXIT){
                list.set(randomInt, TILEROLE.EXIT);
            }else{
                list.set((randomInt+(width-1)/2)%width-1, TILEROLE.EXIT);
            }
        }
        //test
        //Set waypoints
        for(int i = 0; i < Math.max(3,Math.min(waypoints, (length-2)/2)); i++){
            RecurseRandom(list);
        }
    }

    private void RecurseRandom(ArrayList<TILEROLE> list){
        int randomIntLength = ThreadLocalRandom.current().nextInt(1, length-2);
        int randomIntWidth = ThreadLocalRandom.current().nextInt(0, width-1);
        list = generatedMaze.get(randomIntLength);
        if(!list.contains(TILEROLE.WAYPOINT)){
            list.set(randomIntWidth, TILEROLE.WAYPOINT);
        }else{
            RecurseRandom(list);
        }
    }

    public ArrayList<ArrayList<TILEROLE>> getMaze(){
        return generatedMaze;
    }
}
