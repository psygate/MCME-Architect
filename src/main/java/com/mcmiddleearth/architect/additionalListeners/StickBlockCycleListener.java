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
package com.mcmiddleearth.architect.additionalListeners;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.pluginutil.EventUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class StickBlockCycleListener implements Listener {

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block block = event.getClickedBlock();
        if((event.getAction().equals(Action.RIGHT_CLICK_BLOCK )
                       || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
              && p.getInventory().getItemInHand().getType().equals(Material.STICK)
              && EventUtil.isMainHandEvent(event)) {
            if(!PluginData.isModuleEnabled(p.getWorld(),Modules.CYCLE_BLOCKS)) {
                sendNotEnabledErrorMessage(p);
                return;
            }   
            if(!PluginData.hasPermission(p,Permission.CYCLE_BLOCKS)) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                return;
            } else if(!PluginData.hasGafferPermission(p,block.getLocation())) {
                PluginData.getMessageUtil().sendErrorMessage(p, 
                        PluginData.getGafferProtectionMessage(p, block.getLocation()));
                return;
            }
            BlockState state = block.getState();
            int change = 1;
            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                change = -1;
            }
            switch(block.getType()) {
                case CROPS:
                case SNOW:
                case PUMPKIN_STEM:
                case MELON_STEM:
                    state.setRawData((byte)(((8+state.getRawData()+change)%8)));
                    break;
                case CAKE_BLOCK:
                    state.setRawData((byte)(((7+state.getRawData()+change)%7)));
                    break;
                case POTATO:
                case CARROT:
                    if(state.getRawData()>3) {
                        state.setRawData((byte)(((4+state.getRawData()+change)%4)+4));
                    } else {
                        state.setRawData((byte)(((4+state.getRawData()+change)%4)));
                    }
                    break;
                case BEETROOT_BLOCK:
                case CAULDRON:
                    state.setRawData((byte)(((4+state.getRawData()+change)%4)));
                    break;
                case VINE:
                    state.setRawData((byte)(((16+state.getRawData()+change)%16)));
                    break;
                default:
                    return;
            }
            event.setCancelled(true);
            state.update(true, false);
        }
    }

    private void sendNotEnabledErrorMessage(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "Block editor is not enabled for this world.");
    }
        

}
