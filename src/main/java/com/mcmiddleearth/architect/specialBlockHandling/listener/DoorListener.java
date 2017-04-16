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
package com.mcmiddleearth.architect.specialBlockHandling.listener;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.DoorUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.Door;

/**
 *
 * @author Eriol_Eandur
 */
public class DoorListener implements Listener{
    
    /**
     * If module HALF_DOORS is enabled in world config file
     * prevents players from opening half doors.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void noOpenHalfDoors(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player p = event.getPlayer();
            Material blockType = event.getClickedBlock().getType();
            if (DoorUtil.isDoor(blockType)) {
                if(PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.HALF_DOORS)) {
                    DevUtil.log(2,"noOpenHalfDoors fired cancelled: " + event.isCancelled());
                    if(event.isCancelled()) {
                        return;
                    }
                    Block above = event.getClickedBlock().getRelative(BlockFace.UP);
                    if(!DoorUtil.isUpperDoorPart(event.getClickedBlock()) && !(DoorUtil.isDoor(above.getType()) && DoorUtil.isUpperDoorPart(above))){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    /**
     * Opens double doors simulaneously.
     * @param event 
     */
    @EventHandler(priority = EventPriority.LOWEST) 
    public void interactAdjacentDoors(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getHand().equals(EquipmentSlot.HAND)
                && DoorUtil.isDoorBlock(event.getClickedBlock())) {
            Block block = event.getClickedBlock();
            if(DoorUtil.isThinWall(block)) {
                return;
            }
            event.setCancelled(true);
            if(DoorUtil.isUpperDoorBlock(block)) {                       //click at upper door part
                block = block.getRelative(BlockFace.DOWN);      //check if clicked block is part of full door
                if(!DoorUtil.isDoorBlock(block)) {
                    return;
                }
            } else {                                            //click at lower door part
                if(!DoorUtil.isDoorBlock(block.getRelative(BlockFace.UP))//check for half door
                    && !DoorUtil.isFullDoorBelow(block)) {               //check for 3 block high door
                    return;
                }
            }
            if(DoorUtil.isFullDoorBelow(block)) {
                block = block.getRelative(BlockFace.DOWN, 2);   //
            }
            DoorUtil.toggleDoor(block);
            DoorUtil.toggleDoor(block.getRelative(BlockFace.UP));
            DoorUtil.toggleDoor(block.getRelative(BlockFace.UP,2));

            Block block2Lower = DoorUtil.getSecondHalf(block);
            Block block2Upper = block2Lower.getRelative(BlockFace.UP);
            BlockState block2LowerState = block2Lower.getState();
            BlockState block2UpperState = block2Upper.getState();
            if(DoorUtil.isDoorBlock(block2Lower) && DoorUtil.isDoorBlock(block2Upper) 
                && (((Door)block2LowerState.getData()).getFacing()
                            .equals(((Door)block.getState().getData()).getFacing())) //check for same facing
                && (((Door)block2UpperState.getData()).getHinge()           //check for opposite hinge
                       !=(((Door)block.getRelative(BlockFace.UP).getState().getData()).getHinge()))) {
                DoorUtil.toggleDoor(block2Lower);
                DoorUtil.toggleDoor(block2Upper);
                DoorUtil.toggleDoor(block2Lower.getRelative(BlockFace.UP,2));
            }
        }
    }
    
    /*@EventHandler(priority = EventPriority.LOWEST) 
    public void interactPoweredDoors(PlayerInteractEvent event) {
        if(event.hasBlock() && event.getClickedBlock().getType().equals(Material.ACACIA_DOOR)) {
            Block block = event.getClickedBlock();
            if(block.getData() > 9) {
                Block lower = block.getRelative(BlockFace.DOWN);
                if(lower.getType().equals(block.getType())) {
                    if(lower.getData()>3) {
                        closeDoor(lower);
                    } else {
                        openDoor(lower);
                    }
                }
            } else if(block.getData()<8) {
                Block upper = block.getRelative(BlockFace.UP);
                if(upper.getType().equals(block.getType())) {
                    if(block.getData()>3) {
                        closeDoor(block);
                    } else {
                        openDoor(block);
                    }
                }
            }
        }
    }*/
    
}
