/*
 * Copyright (C) 2017 MCME
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
package com.mcmiddleearth.util;

import com.mcmiddleearth.architect.PluginData;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;

/**
 *
 * @author Eriol_Eandur
 */
public class DoorUtil {
    
    public static boolean isUpperDoorPart(Block block) { //doesnt'work for powered doors
        return block.getData()==8 || block.getData()==9; 
    }

    public static boolean isDoor(Material blockType) {
        return blockType.equals(Material.WOODEN_DOOR)
                || blockType.equals(Material.IRON_DOOR_BLOCK)
                || blockType.equals(Material.SPRUCE_DOOR)
                || blockType.equals(Material.BIRCH_DOOR)
                || blockType.equals(Material.JUNGLE_DOOR)
                || blockType.equals(Material.ACACIA_DOOR)
                || blockType.equals(Material.DARK_OAK_DOOR);
    }

    public static boolean isThinWall(Block block) {
        if(isUpperDoorBlock(block)) {
            return isDoorBlock(block) && PluginData.getNoInteraction(block);//!(block.getType().equals(Material.IRON_DOOR_BLOCK) 
                                          //|| block.getType().equals(Material.BIRCH_DOOR))
                                      //&& block.getData()>9;                                //check powered state
        } else {
            block = block.getRelative(BlockFace.UP);
            return isUpperDoorBlock(block) && PluginData.getNoInteraction(block);//!(block.getType().equals(Material.BIRCH_DOOR) 
                                               //|| block.getType().equals(Material.IRON_DOOR_BLOCK))
                                           //&& block.getData()>9;                                //check powered state
        }
    }
    public static boolean isUpperDoorBlock(Block block) {
        return isDoorBlock(block)
              && ((Door)block.getState().getData()).isTopHalf();
    }
    
    public static boolean isLowerDoorBlock(Block block) {
        return  isDoorBlock(block)
                && !isUpperDoorBlock(block);
    }
    
    public static boolean isDoorBlock(Block block) {
        return block.getState().getData() instanceof Door;
    }
    
    public static boolean isFullDoorAbove(Block block) {
        return   isUpperDoorBlock(block.getRelative(BlockFace.UP,2))
                && isLowerDoorBlock(block.getRelative(BlockFace.UP));
    }
    
    public static Block getSecondHalf(Block block) {
        if(((Door)block.getRelative(BlockFace.UP).getState().getData()).getHinge()) {
            switch(((Door) block.getState().getData()).getFacing()) {
                case NORTH: return block.getRelative(BlockFace.EAST);
                case EAST: return block.getRelative(BlockFace.SOUTH);
                case SOUTH: return block.getRelative(BlockFace.WEST);
                case WEST: return block.getRelative(BlockFace.NORTH);
            }
        } else {
            switch(((Door) block.getState().getData()).getFacing()) {
                case NORTH: return block.getRelative(BlockFace.WEST);
                case EAST: return block.getRelative(BlockFace.NORTH);
                case SOUTH: return block.getRelative(BlockFace.EAST);
                case WEST: return block.getRelative(BlockFace.SOUTH);
            }
        }
        return null;
    }
    
    public static void toggleDoor(Block block) {
        if(block.getState().getData() instanceof Door
              && !((Door)block.getState().getData()).isTopHalf()
              && !(isThinWall(block))) {
//Logger.getGlobal().info("toggle "+block.getX()+" "+block.getY()+" "+block.getZ());
            BlockState state = block.getState();
//Logger.getGlobal().info("dv "+state.getRawData());
            Door data = (Door) state.getData();
            if(isUpperDoorBlock(block.getRelative(BlockFace.UP))) {
                data.setOpen(!data.isOpen());
                state.setData(data);
                state.update();
            }
//Logger.getGlobal().info("dv new "+state.getRawData());
        }
    }
    public static void toggleHalfDoor(Block block, boolean hingeRight, boolean isOpen) {
//Logger.getGlobal().info("toggle "+block.getX()+" "+block.getY()+" "+block.getZ()+" "+hingeRight+" "+isOpen);
        if(block.getState().getData() instanceof Door
              && !((Door)block.getState().getData()).isTopHalf()) {
            BlockState state = block.getState();
            Door data = (Door) state.getData();
//Logger.getGlobal().info("toggle 2");
            BlockFace facing = data.getFacing();
            boolean clockwise = hingeRight ^ isOpen; //XOR
            facing = rotate(facing, clockwise);
            data.setFacingDirection(facing);
            state.setData(data);
            state.update();
//Logger.getGlobal().info("toggle !");
        }
    }
    
    private static BlockFace rotate(BlockFace facing, boolean clockwise) {
        if(clockwise) {
            switch(facing) {
                case NORTH:
                    return BlockFace.EAST;
                case EAST:
                    return BlockFace.SOUTH;
                case SOUTH:
                    return BlockFace.WEST;
                case WEST:
                    return BlockFace.NORTH;
            }
        } else {
            switch(facing) {
                case NORTH:
                    return BlockFace.WEST;
                case EAST:
                    return BlockFace.NORTH;
                case SOUTH:
                    return BlockFace.EAST;
                case WEST:
                    return BlockFace.SOUTH;
            }
        }
        return BlockFace.DOWN;
    }
    
    private static void closeDoor(Block block) {
        block.setData((byte)(block.getData()-4), false);
    }
    
    private static void openDoor(Block block) {
        block.setData((byte)(block.getData()+4), false);
    }
    
}
