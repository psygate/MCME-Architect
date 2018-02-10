/*
 * Copyright (C) 2016 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.architect.specialBlockHandling.specialBlocks;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockType;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.DoorUtil;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockDoor extends SpecialBlock {
    
   private boolean powered, hingeRight;
    
    private SpecialBlockDoor(String id, 
                        Material material, boolean powered, boolean hingeRight) {
        super(id, material, (byte) 0, SpecialBlockType.DOOR);
        this.powered = powered;
        this.hingeRight = hingeRight;
    }
    
    protected SpecialBlockDoor(String id, 
                        Material material, boolean powered, boolean hingeRight,
                        SpecialBlockType type) {
        super(id, material, (byte) 0, type);
        this.powered = powered;
        this.hingeRight = hingeRight;
    }
    
    protected SpecialBlockDoor(String id, 
                        Material material, 
                        byte dataValue,
                        SpecialBlockType type) {
        super(id, material, (byte) 0, SpecialBlockType.DOOR);
        powered = false;
        hingeRight = false;
    }
    
    public static SpecialBlockDoor loadFromConfig(ConfigurationSection config, String id) {
        Material material = matchMaterial(config.getString("blockMaterial",""));
        if(material==null) {
            return null;
        }
        boolean powered = config.getBoolean("powered", false);
        boolean hingeRight = config.getBoolean("hingeRight", false);
        return new SpecialBlockDoor(id, material, powered, hingeRight);
    }
    
    @Override
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Player player) {
        final Location playerLoc = player.getLocation();
        placeDoor(blockPlace, playerLoc, getMaterial(), powered, false, hingeRight, false);
    }
    
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, 
                           final Player player, boolean hingeRight) {
        final Location playerLoc = player.getLocation();
        placeDoor(blockPlace, playerLoc, getMaterial(), powered, false, hingeRight, false);
    }
    
    protected void placeDoor(final Block blockPlace, final Location playerLoc,
                             Material material, final boolean powered,
                             final boolean fixedHinge, final boolean hingeRight,
                             final boolean open) {
        final BlockState lowerState = blockPlace.getState();
        lowerState.setType(material);
        final BlockState upperState = blockPlace.getRelative(BlockFace.UP).getState();
        upperState.setType(material);
        new BukkitRunnable() {
            @Override
            public void run() {
                lowerState.update(true, false);
                upperState.update(true, false);
                DevUtil.log("4 door block place: ID "+lowerState.getType()+" - DV "+lowerState.getRawData());
                DevUtil.log("4 door block place: ID "+upperState.getType()+" - DV "+upperState.getRawData());
                final BlockState tempLowerState = lowerState.getBlock().getState();
                final BlockState tempUpperState = upperState.getBlock().getState();
                if(tempLowerState.getData() instanceof Door && tempUpperState.getData() instanceof Door) {
                    Door lowerDoorData = (Door) tempLowerState.getData();
                    Door upperDoorData = (Door) tempUpperState.getData();
                    upperDoorData.setTopHalf(true);
                    float yaw = playerLoc.getYaw();
                    if(open) {
                        if(hingeRight) {
                            yaw -= 90;
                        } else {
                            yaw += 90;
                        }
                    }
                    final BlockFace facing = getBlockFace(yaw);//getOppositeBlockFace(getBlockFace(yaw));
                    lowerDoorData.setFacingDirection(facing);
                    lowerDoorData.setOpen(open);
                    tempLowerState.setData(lowerDoorData);
                    boolean hinge = hingeRight;
                    if(!fixedHinge) {
                        hinge = checkForHingeRightSide(tempUpperState.getBlock(),facing, hingeRight);
                    }
                    //upperDoorData.setHinge(!hinge);
                    if(powered && hinge) {
                        upperDoorData.setData((byte)10);
                    } else if(powered && !hinge) {
                        upperDoorData.setData((byte)11);
                    } else if(!powered && hinge) {
                        upperDoorData.setData((byte)8);
                    } else { 
                        upperDoorData.setData((byte)9);
                    }
                    tempUpperState.setData(upperDoorData);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            DevUtil.log("4 door block place x2: ID "+tempLowerState.getType()+" - DV "+tempLowerState.getRawData());
                            DevUtil.log("4 door block place x2: ID "+tempUpperState.getType()+" - DV "+tempUpperState.getRawData());
                            tempLowerState.update(true, false);
                            tempUpperState.update(true, false);
                        }
                    }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
                }
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
    }
 
    private boolean checkForHingeRightSide(Block block, BlockFace facing, boolean hingeRight) {
Logger.getGlobal().info("DoorBlockPlace hinge side right: "+hingeRight);
        Block leftBlock, rightBlock;
        switch(facing) {
            case NORTH: rightBlock = block.getRelative(BlockFace.EAST);
                     leftBlock = block.getRelative(BlockFace.WEST);break;
            case EAST: rightBlock = block.getRelative(BlockFace.SOUTH);
                     leftBlock = block.getRelative(BlockFace.NORTH); break;
            case SOUTH: rightBlock = block.getRelative(BlockFace.WEST);
                     leftBlock = block.getRelative(BlockFace.EAST); break;
            default: rightBlock = block.getRelative(BlockFace.NORTH);
                     leftBlock = block.getRelative(BlockFace.SOUTH);
        }
        if(hingeRight) {
            BlockState checkState = leftBlock.getState();
//    Logger.getGlobal().info("DoorBlockPlace leftState id "+checkState.getType());
            if(checkState.getData() instanceof Door && ((Door)checkState.getData()).getHinge()) {
//    Logger.getGlobal().info("DoorBlockPlace leftState return true");
                return true;
            }
            checkState = rightBlock.getState();
//    Logger.getGlobal().info("DoorBlockPlace rightState id "+checkState.getType());
            if(checkState.getData() instanceof Door && !((Door)checkState.getData()).getHinge()) {
//    Logger.getGlobal().info("DoorBlockPlace rightState return false");
                return false;
            }
        }
        else {
            BlockState checkState = rightBlock.getState();
//    Logger.getGlobal().info("DoorBlockPlace rightState id "+checkState.getType());
            if(checkState.getData() instanceof Door && !((Door)checkState.getData()).getHinge()) {
//    Logger.getGlobal().info("DoorBlockPlace rightState return false");
                return false;
            }
            checkState = leftBlock.getState();
//    Logger.getGlobal().info("DoorBlockPlace leftState id "+checkState.getType());
            if(checkState.getData() instanceof Door && ((Door)checkState.getData()).getHinge()) {
//    Logger.getGlobal().info("DoorBlockPlace leftState return true");
                return true;
            }
        }
Logger.getGlobal().info("DoorBlockPlace leftState return default.");
        return hingeRight;
    }
    
   @Override
    public boolean matches(Block block) {
        if(getMaterial().equals(block.getType())) {
           if(DoorUtil.isLowerDoorBlock(block)) {
               block = block.getRelative(BlockFace.UP);
               if(!DoorUtil.isUpperDoorBlock(block)) {
                   return false;
               }
           } else {
               if(!DoorUtil.isLowerDoorBlock(block.getRelative(BlockFace.DOWN))) {
                   return false;
               }
           }
           return powered == (block.getData()>9);
        }
        return false;
    }
    
}
