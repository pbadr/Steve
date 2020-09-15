package com.steve.game.tiptoe;

import com.steve.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

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

    public void placeBlocks() {
        World world = position.getWorld();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                Location location = new Location(world, position.getBlockX() + x ,position.getBlockY() - 1, position.getBlockZ() + z);
                location.getBlock().setType(material);

                assert world != null;
                world.spawnParticle(Particle.EXPLOSION_NORMAL, location.getX(), location.getY(), location.getZ(), 10);
            }
        }
    }
}
