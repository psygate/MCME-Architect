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
import com.mcmiddleearth.architect.PluginData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class GameMechanicsListener implements Listener{
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        PluginData.configureWorld(event.getWorld());
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (PluginData.isModuleEnabled(event.getWorld(), Modules.WEATHER_BLOCKING)) {
            if(!PluginData.isOverrideWeather()) {
//Logger.getGlobal().info("Cancelling weather change.");
                event.setCancelled(true);
            } else {
//Logger.getGlobal().info("Allow weather change.");
            }
            PluginData.setOverrideWeather(false);
        }
    }
    
    @EventHandler
    public void blockPlayerDrops(PlayerDropItemEvent event)
    {
        if(PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.DROP_BLOCKING)) {
            event.getItemDrop().remove();
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        if(PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.FIRE_SPREAD_BLOCKING))
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
//Logger.getGlobal().info("Block decay: "+event.getBlock().getType().name()+" "
//                                       +event.getBlock().getX()
//                                       +" "+event.getBlock().getZ());
    if (PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.DECAY_BLOCKING)) {
//Logger.getGlobal().info("Block decay canceled");
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.DECAY_BLOCKING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        if (PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BLOCK_FORM_BLOCKING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        if (PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BLOCK_FORM_BLOCKING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        if (PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BLOCK_FORM_BLOCKING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        if (PluginData.isModuleEnabled(event.getWorld(), Modules.BLOCK_FORM_BLOCKING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMoistureChange(MoistureChangeEvent event) {
        if (PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BLOCK_FORM_BLOCKING)) {
            event.setCancelled(true);
        }
    }

    //@EventHandler(ignoreCancelled = true)
    //public void onFurnaceBurn(FurnaceBurnEvent event) {
//Logger.getGlobal().info("Furnace "+event.isBurning());
        
    //}
}
