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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.Door;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockDoor extends SpecialBlock {
    
   private final boolean powered;
    
    private SpecialBlockDoor(String id, 
                        Material material, boolean powered) {
        super(id, material, (byte) 0, SpecialBlockType.DOOR);
        this.powered = powered;
    }
    
    protected SpecialBlockDoor(String id, 
                        Material material, 
                        byte dataValue,
                        SpecialBlockType type) {
        super(id, material, (byte) 0, SpecialBlockType.DOOR);
        powered = false;
    }
    
    public static SpecialBlockDoor loadFromConfig(ConfigurationSection config, String id) {
        Material material = matchMaterial(config.getString("blockMaterial",""));
        if(material==null) {
            return null;
        }
        boolean powered = config.getBoolean("powered", false);
        return new SpecialBlockDoor(id, material, powered);
    }
    
    @Override
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Location playerLoc) {
        placeDoor(blockPlace, playerLoc, getMaterial(), powered, false, false);
    }
    
    protected void placeDoor(final Block blockPlace, final Location playerLoc,
                             Material material, final boolean powered,
                             final boolean fixedHinge, final boolean hingeRight) {
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
                    final BlockFace facing = getOppositeBlockFace(getBlockFace(playerLoc.getYaw()));
                    lowerDoorData.setFacingDirection(facing);
                    tempLowerState.setData(lowerDoorData);
                    boolean hinge;
                    if(fixedHinge) {
                        hinge = hingeRight;
                    } else {
                        hinge = checkForHingeRightSide(tempUpperState.getBlock(),facing);
                    }
                    upperDoorData.setHinge(hinge);
                    if(powered) {
                        upperDoorData.setData((byte)(upperDoorData.getData()+2));
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
 
    private boolean checkForHingeRightSide(Block block, BlockFace facing) {
        Block checkBlock;
        switch(facing) {
            case NORTH: checkBlock = block.getRelative(BlockFace.EAST); break;
            case EAST: checkBlock = block.getRelative(BlockFace.SOUTH); break;
            case SOUTH: checkBlock = block.getRelative(BlockFace.WEST); break;
            default: checkBlock = block.getRelative(BlockFace.NORTH);
        }
        BlockState checkState = checkBlock.getState();
        if(checkState.getData() instanceof Door) {
            return !((Door)checkState.getData()).getHinge();
        } else {
            return false;
        }
    }
    
   @Override
    public boolean matches(Block block) {
        if(getMaterial().equals(block.getType())) {
           if(DoorUtil.isLowerDoorBlock(block)) {
               block = block.getRelative(BlockFace.UP);
               if(!DoorUtil.isUpperDoorBlock(block)) {
                   return false;
               }
           }
           return powered == (block.getData()>9);
        }
        return false;
    }

}
