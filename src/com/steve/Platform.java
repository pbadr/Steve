package com.steve;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Platform {
    int width;
    int length;
    Location position;
    Material material;
    boolean isFake;
    ArrayList<Block> tileBlocks;

    public Platform(int width, int length, Location position, Material material, boolean isFake){
        this.width = width;
        this.length = length;
        this.position = position;
        this.material = material;
        this.isFake = isFake;
    }

    public void placeBlocks(){
        World w = position.getWorld();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                Location l = new Location(w,position.getBlockX()+x,position.getBlockY(),position.getBlockZ()+z);
                l.getBlock().setType(material);
            }
        }
    }


}
