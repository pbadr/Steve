package com.steve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Maze {
    private int width;
    private int length;
    private int waypoints;
    private int deadends;
    private ArrayList<ArrayList<Boolean>> generatedMaze;

    public Maze(int width, int length, int waypoints, int deadends){
        this.width = width;
        this.length = length;
        this.waypoints = waypoints;
        this.deadends = deadends;

        generatedMaze = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            ArrayList<Boolean> list = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                list.add(false);
            }

            generatedMaze.add(list);
        }
    }

    public void generateMaze(){

    }

    public ArrayList<ArrayList<Boolean>> getMaze(){
        return generatedMaze;
    }
}
