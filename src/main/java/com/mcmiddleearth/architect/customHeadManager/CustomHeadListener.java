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
package com.mcmiddleearth.architect.customHeadManager;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.HeadUtil;
import com.mcmiddleearth.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class CustomHeadListener implements Listener {
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!(player.getInventory().getItemInMainHand().getType().equals(Material.STICK) 
                && event.getHand().equals(EquipmentSlot.HAND))) {
            return;
        }
        if(!(event.hasBlock() && event.getClickedBlock().getType().equals(Material.SKULL)
                              && ((Skull)event.getClickedBlock().getState()).getSkullType().equals(SkullType.PLAYER))) {
            return;
        }
        if(!PluginData.isModuleEnabled(player.getWorld(), Modules.CUSTOM_HEAD_MANAGER)) {
            sendNotActivatedMessage(player);
            return;
        }
        if(!PluginData.hasPermission(player,Permission.CUSTOM_HEAD_USER)) {
            CommonMessages.sendNoPermissionError(player);
            return;
        }
        ItemStack head = HeadUtil.pickCustomHead((Skull) event.getClickedBlock().getState());
        player.getInventory().addItem(head);
        MessageUtil.sendInfoMessage(player,"Given head: "+MessageUtil.STRESSED+head.getItemMeta().getDisplayName());
    }
    
    private void sendNotActivatedMessage(Player player) {
        MessageUtil.sendErrorMessage(player,"Custom Heads are not enabled for this world.");
    }

}
