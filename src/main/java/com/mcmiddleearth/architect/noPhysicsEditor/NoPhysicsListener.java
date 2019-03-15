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
package com.mcmiddleearth.architect.noPhysicsEditor;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.watcher.WatchedListener;
import com.mcmiddleearth.pluginutil.BlockUtil;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.TheGafferUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class NoPhysicsListener extends WatchedListener{
    
    @EventHandler
    private void noPhysicsList(BlockPhysicsEvent event) {
        if(PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.NO_PHYSICS_LIST_ENABLED)
                && NoPhysicsData.isNoPhysicsBlock(event.getBlock())
                && !NoPhysicsData.hasNoPhysicsException(event.getBlock())) {
            DevUtil.log(4,"no Physics "+event.getBlock().getType().name()+" "+event.getBlock().getX()+" "+event.getBlock().getY()+" "+event.getBlock().getZ()+" "+event.getChangedType());
            event.setCancelled(true);
        } else {
            DevUtil.log(4,"allow Physics "+event.getBlock().getType().name()+" "+event.getBlock().getX()+" "+event.getBlock().getY()+" "+event.getBlock().getZ()+" "+event.getChangedType());
        }            
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlaceNoPhysicsListBlock(BlockPlaceEvent event) {
        if(event.canBuild() 
                && TheGafferUtil.hasGafferPermission(event.getPlayer(),event.getBlock().getLocation())) {
            connectNoPhysicsBlocks(event.getBlock());
        }
    }
    
    public static void connectNoPhysicsBlocks(Block block) {
        /*if(PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_LIST_ENABLED)
                && NoPhysicsData.isNoPhysicsBlock(block)
                && !NoPhysicsData.hasNoPhysicsException(block)) {*/
            DevUtil.log(4,"place no physics block"+block.getType().name()+" "+block.getX()+" "+block.getZ());
            if(((block.getBlockData() instanceof Fence) 
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_FENCES))
               || ((block.getBlockData() instanceof GlassPane) 
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_GLASS))) {
                MultipleFacing data = (MultipleFacing) block.getBlockData();
                for(BlockFace face: data.getAllowedFaces()) {
                    Block neighbour = block.getRelative(face);
                    if(neighbour.getBlockData().getClass().equals(data.getClass())) {
                        MultipleFacing neighbourData = (MultipleFacing)neighbour.getBlockData();
                        neighbourData.setFace(BlockUtil.rotateBlockFace(face,2), true);
                        neighbour.setBlockData(neighbourData,false);
                    }
                }
            } else if((block.getBlockData() instanceof Stairs) 
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_STAIRS)) {
                Stairs stair = (Stairs) block.getBlockData();
                BlockFace face = stair.getFacing();
                Block left = block.getRelative(BlockUtil.rotateBlockFace(face,1));
                if(left.getBlockData() instanceof Stairs) {
                    Stairs leftStair = (Stairs) left.getBlockData();
                    if(leftStair.getFacing().equals(BlockUtil.rotateBlockFace(face,3))) {
                        leftStair.setShape(Stairs.Shape.OUTER_RIGHT);
                        left.setBlockData(leftStair,false);
                    } else if(leftStair.getFacing().equals(BlockUtil.rotateBlockFace(face,1))) {
                        leftStair.setShape(Stairs.Shape.INNER_LEFT);
                        left.setBlockData(leftStair,false);
                    }
                }
                Block right = block.getRelative(BlockUtil.rotateBlockFace(face,3));
                    if(right.getBlockData() instanceof Stairs) {
                    Stairs rightStair = (Stairs) right.getBlockData();
                    if(rightStair.getFacing().equals(BlockUtil.rotateBlockFace(face,3))) {
                        rightStair.setShape(Stairs.Shape.INNER_RIGHT);
                        right.setBlockData(rightStair,false);
                    } else if(rightStair.getFacing().equals(BlockUtil.rotateBlockFace(face,1))) {
                        rightStair.setShape(Stairs.Shape.OUTER_LEFT);
                        right.setBlockData(rightStair,false);
                    }
                }                    
            } else if(block.getType().equals(Material.CHORUS_PLANT)
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_CHORUS)) {
                MultipleFacing data = (MultipleFacing) block.getBlockData();
                for(BlockFace face: data.getAllowedFaces()) {
                    Block neighbour = block.getRelative(face);
                    if(neighbour.getType().equals(Material.CHORUS_PLANT)) {
                        MultipleFacing blockData = (MultipleFacing)block.getBlockData();
                        MultipleFacing neighbourData = (MultipleFacing)neighbour.getBlockData();
                        switch(face) {
                            case UP:
                                neighbourData.setFace(BlockFace.DOWN, true);
                                break;
                            case DOWN:
                                neighbourData.setFace(BlockFace.UP, true);
                                break;
                            default:
                                neighbourData.setFace(BlockUtil.rotateBlockFace(face, 2), true);
                        }
                        neighbour.setBlockData(neighbourData,false);
                        blockData.setFace(face, true);
                        block.setBlockData(blockData,false);
                    }
                }
            }
        //}            
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onBreakNoPhysicsListBlock(BlockBreakEvent event) {
        if(!TheGafferUtil.hasGafferPermission(event.getPlayer(),event.getBlock().getLocation())) {
            return;
        }
        /*if(PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.NO_PHYSICS_LIST_ENABLED)
                && NoPhysicsData.isNoPhysicsBlock(event.getBlock())
                && !NoPhysicsData.hasNoPhysicsException(event.getBlock())) {*/
            DevUtil.log(4,"break no physics block"+event.getBlock().getType().name()+" "+event.getBlock().getX()+" "+event.getBlock().getZ());
            Block block = event.getBlock();
            if((block.getBlockData() instanceof Fence) 
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_FENCES)) {
                Fence fence = (Fence) block.getBlockData();
                for(BlockFace face: fence.getAllowedFaces()) {
                    Block neighbour = block.getRelative(face);
                    if(neighbour.getBlockData() instanceof Fence) {
                        Fence neighbourFence = (Fence) neighbour.getBlockData();
                        neighbourFence.setFace(BlockUtil.rotateBlockFace(face,2), false);
                        neighbour.setBlockData(neighbourFence,false);
                    }
                }
            } else if((block.getBlockData() instanceof Stairs) 
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_STAIRS)) {
                Stairs stair = (Stairs) block.getBlockData();
                BlockFace face = stair.getFacing();
                Block left = block.getRelative(BlockUtil.rotateBlockFace(face,1));
                if(left.getBlockData() instanceof Stairs) {
                    Stairs leftStair = (Stairs) left.getBlockData();
                    if(leftStair.getFacing().equals(BlockUtil.rotateBlockFace(face,3))
                            && leftStair.getShape().equals(Stairs.Shape.OUTER_RIGHT)) {
                        leftStair.setShape(Stairs.Shape.STRAIGHT);
                        left.setBlockData(leftStair,false);
                    } else if(leftStair.getFacing().equals(BlockUtil.rotateBlockFace(face,1))
                            &&leftStair.getShape().equals(Stairs.Shape.INNER_LEFT)) {
                        leftStair.setShape(Stairs.Shape.STRAIGHT);
                        left.setBlockData(leftStair,false);
                    }
                }
                Block right = block.getRelative(BlockUtil.rotateBlockFace(face,3));
                if(right.getBlockData() instanceof Stairs) {
                    Stairs rightStair = (Stairs) right.getBlockData();
                    if(rightStair.getFacing().equals(BlockUtil.rotateBlockFace(face,3))
                            && rightStair.getShape().equals(Stairs.Shape.INNER_RIGHT)) {
                        rightStair.setShape(Stairs.Shape.STRAIGHT);
                        right.setBlockData(rightStair,false);
                    } else if(rightStair.getFacing().equals(BlockUtil.rotateBlockFace(face,1))
                            && rightStair.getShape().equals(Stairs.Shape.OUTER_LEFT)) {
                        rightStair.setShape(Stairs.Shape.STRAIGHT);
                        right.setBlockData(rightStair,false);
                    }
                }
            } else if(block.getType().equals(Material.CHORUS_PLANT)
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_CHORUS)) {
                MultipleFacing data = (MultipleFacing) block.getBlockData();
                for(BlockFace face: data.getAllowedFaces()) {
                    Block neighbour = block.getRelative(face);
                    if(neighbour.getType().equals(Material.CHORUS_PLANT)) {
                        MultipleFacing neighbourData = (MultipleFacing)neighbour.getBlockData();
                        neighbourData.setFace(BlockUtil.rotateBlockFace(face,2), false);
                        neighbour.setBlockData(neighbourData,false);
                    }
                }
            } else if((block.getType().equals(Material.CHEST)
                        || block.getType().equals(Material.TRAPPED_CHEST)) 
                    && PluginData.isModuleEnabled(block.getWorld(), Modules.NO_PHYSICS_CONNECT_CHESTS)) {
                Chest chest = (Chest) block.getBlockData();
                if(chest.getType().equals(Chest.Type.LEFT)) {
                    Block neighbour = block.getRelative(BlockUtil.rotateBlockFace(chest.getFacing(), 1));
                    BlockData neighbourData = neighbour.getBlockData();
                    if(neighbourData instanceof Chest
                            && ((Chest)neighbourData).getType().equals(Chest.Type.RIGHT)) {
                        ((Chest)neighbourData).setType(Chest.Type.SINGLE);
                        neighbour.setBlockData(neighbourData, false);
                    }
                } else if(chest.getType().equals(Chest.Type.RIGHT)) {
                    Block neighbour = block.getRelative(BlockUtil.rotateBlockFace(chest.getFacing(), 3));
                    BlockData neighbourData = neighbour.getBlockData();
                    if(neighbourData instanceof Chest
                            && ((Chest)neighbourData).getType().equals(Chest.Type.LEFT)) {
                        ((Chest)neighbourData).setType(Chest.Type.SINGLE);
                        neighbour.setBlockData(neighbourData, false);
                    }
                }
            }
        //}            
    }
}
