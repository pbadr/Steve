package com.steve;

import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Maze {

    public enum TILEROLE{
        ENTRANCE,
        WAYPOINT,
        PATH,
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

        ArrayList<Vector> entranceList  = new ArrayList<>(),
                exitList  = new ArrayList<>(),
                deadendList  = new ArrayList<>(),
                waypointList = new ArrayList<>();

        //Set entrances

        ArrayList<TILEROLE> row = generatedMaze.get(0);

        for(int i = 0; i < entrances;i++){

            int randomInt = ThreadLocalRandom.current().nextInt(0,width);

            if(row.get(randomInt) != TILEROLE.ENTRANCE){
                row.set(randomInt, TILEROLE.ENTRANCE);
                entranceList.add(new Vector().setX(0).setY(randomInt));
            }else{
                row.set((randomInt+width/2)%width, TILEROLE.ENTRANCE);
                entranceList.add(new Vector().setX(0).setY((randomInt+(width-1)/2)%width-1));
            }
        }

        //Set exits

        row = generatedMaze.get(width-1);

        for(int i = 0; i < exits; i++){

            int randomInt = ThreadLocalRandom.current().nextInt(0,width);

            if(row.get(randomInt) != TILEROLE.EXIT){
                row.set(randomInt, TILEROLE.EXIT);
                exitList.add(new Vector().setX(width).setY(randomInt));
            }else{
                row.set((randomInt+width/2)%width, TILEROLE.EXIT);
                exitList.add(new Vector().setX(width).setY((randomInt+(width-1)/2)%width-1));
            }
        }

        //Set waypoints

        for(int i = 0; i < Math.max(3,Math.min(waypoints, (length-2)/2)); i++){
            RecurseRandomWaypoints(row);
        }

        //Set deadends

        for(int i = 0; i < deadends; i++){
            int randomIntWidth = ThreadLocalRandom.current().nextInt(0, width);
            int randomIntLength = ThreadLocalRandom.current().nextInt(1, length-2);

            row = generatedMaze.get(randomIntWidth);
            if(row.get(randomIntLength) != TILEROLE.WAYPOINT){
                row.set(randomIntLength, TILEROLE.DEADEND);
                waypointList.add(new Vector().setX(randomIntWidth).setY(randomIntLength));
            }
        }

        //Astar PathFinding
        ArrayList<ArrayList<Integer>> pathfindGrid = new ArrayList<>();

        //Initialising
        for (int i = 0; i < width; i++) {
            ArrayList<Integer> list = new ArrayList<>();

            for (int j = 0; j < length; j++) {
                list.add(0);
            }

            pathfindGrid.add(list);
        }

        Vector destination;
        ArrayList<Vector> usedWaypoints = new ArrayList<>();
        for (int i = 0; i < entrances; i++) {
            //Get closest waypoint to route it towards
            destination = nearestVector(entranceList.get(i),waypointList);
            Bukkit.getLogger().info("" + (int) destination.getX() + " , " + (int) destination.getY());
            //Insert pathfinding function
            routePathFinding(entranceList.get(i), destination);
            usedWaypoints.add(destination);
        }
        for(Vector v: usedWaypoints){
            waypointList.remove(v);
        }
    }

    private Vector nearestVector(Vector v, ArrayList<Vector> vectors){

        Vector closestVector = null;

        for (Vector i: vectors) {
            if(closestVector == null){
                closestVector = i;
                continue;
            }
            if(i.length() - v.length() < closestVector.length() - v.length()){
                closestVector = i;
            }
        }

        return v;
    }

    private void routePathFinding(Vector a, Vector b){

        int level = (int)a.getY();
        for (int i = (int) a.getX(); i < (int)b.getX(); i++) {
            if(i == (int)b.getX()){
                for (int j = level; j < (int)b.getY(); j++) {
                    generatedMaze.get(i).set(level, TILEROLE.PATH);
                }
                return;
            }
            if(ThreadLocalRandom.current().nextInt(2) == 0 && level != (int)b.getY()){
                level++;
            }
            generatedMaze.get(i).set(level, TILEROLE.PATH);
        }
    }

    private void RecurseRandomWaypoints(ArrayList<TILEROLE> list){
        int randomIntWidth = ThreadLocalRandom.current().nextInt(0, width);
        int randomIntLength = ThreadLocalRandom.current().nextInt(1, length-2);

        list = generatedMaze.get(randomIntLength);

        if(!list.contains(TILEROLE.WAYPOINT)){
            list.set(randomIntWidth, TILEROLE.WAYPOINT);
        }else{
            RecurseRandomWaypoints(list);
        }
    }

    public ArrayList<ArrayList<TILEROLE>> getMaze(){
        return generatedMaze;
    }
}
