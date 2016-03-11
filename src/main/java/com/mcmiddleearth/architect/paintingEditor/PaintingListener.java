/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.paintingEditor;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.MessageUtil;
import java.util.logging.Logger;
import org.bukkit.Art;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class PaintingListener implements Listener {
    
    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
    public void playerInteractEntity(PlayerInteractEntityEvent event) {
Logger.getGlobal().info("animals "+event.getPlayer().getWorld().getAllowAnimals());
Logger.getGlobal().info("mobs "+event.getPlayer().getWorld().getAllowMonsters());
        
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if(!(entity instanceof Painting 
                && player.getItemInHand().getType().equals(Material.STICK))) {
            return;
        }
        if(!PluginData.isModuleEnabled(entity.getWorld(),Modules.PAINTING_EDITOR)) {
            sendNotEnabledErrorMessage(player);
            return;
        }   
        if(PluginData.hasPermission(player,Permission.PAINTING_EDITOR)) {
             {
                Painting painting = (Painting) entity;
                int id = painting.getArt().getId();
                if(id<Art.values().length-1) {
                    id++;
                    painting.setArt(Art.getById(id));
                }
            }
        }
        else {
            CommonMessages.sendNoPermissionError(player);
        }
        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled = true)
    public void hangingBreakByEntity(HangingBreakByEntityEvent event) {
        Entity damager = event.getRemover();
        Entity entity = event.getEntity();
        if(!(damager instanceof Player && entity instanceof Painting) ) {
            return;
        }
        Player player = (Player) damager;
        if(!PluginData.isModuleEnabled(entity.getWorld(),Modules.PAINTING_EDITOR)) {
            sendNotEnabledErrorMessage(player);
            return;
        }   
        if(!player.getItemInHand().getType().equals(Material.STICK)) {
            return;
        }
        if(PluginData.hasPermission(player,Permission.PAINTING_EDITOR)) {
            Painting painting = (Painting) entity;
            int id = painting.getArt().getId();
            if(id>0) {
                id--;
                painting.setArt(Art.getById(id));
            }
        }
        else {
            CommonMessages.sendNoPermissionError(player);
        }
        event.setCancelled(true);
    }

    private void sendNotEnabledErrorMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "Painting editor is not enabled for this world.");
    }
        
}