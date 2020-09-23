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

import com.mcmiddleearth.architect.*;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsData;
import com.mcmiddleearth.architect.serverResoucePack.RpManager;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialBlockInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialHeadInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftInventoryCrafting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class InventoryListener implements Listener{
    
    /**
     * If module SPECIAL_BLOCK_GET is enabled in world config file
     * opens the custom block inventory when a player presses his swap hand item key.
     * Tries to read players metadata set by ResourceRegions plugin. 
     * If there is no metadata tries to open inventory for resource pack "Gondor" as defined
     * in Architect config file.
     * @param event 
     */
    @EventHandler(priority=EventPriority.LOW)  
    public void openSpecialInventory(PlayerSwapHandItemsEvent event) {
        if(PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_GET)) {
            event.setCancelled(true);
            if((event.getPlayer().getOpenInventory().getTopInventory()) instanceof CraftInventoryCrafting) {
                final Player player = event.getPlayer();
                ItemStack handItem = player.getInventory().getItemInMainHand();
                //open custom block inventory
                ItemStack offHandItem = player.getInventory().getItemInOffHand();
                if ((handItem.hasItemMeta() && handItem.getItemMeta().hasDisplayName()
                        && handItem.getItemMeta().getDisplayName().startsWith("Head Inventory"))
                        || (offHandItem.hasItemMeta() && offHandItem.getItemMeta().hasDisplayName()
                        && offHandItem.getItemMeta().getDisplayName().startsWith("Head Inventory"))) {
                    SpecialHeadInventoryData.openInventory(player);
                    return;
                }
                String rpName = RpManager.getCurrentRpName(player);//1.13 removed: PluginData.getRpName(ResourceRegionsUtil.getResourceRegionsUrl(p));
                if (rpName == null || rpName.equals("")) {
                    rpName = SpecialBlockInventoryData.getRpName(handItem);
                    if (rpName.equals("")) {
                        rpName = SpecialBlockInventoryData.getRpName(offHandItem);
                    }
                }
                if (!SpecialBlockInventoryData.openInventory(player, rpName)) {
                    sendNoInventoryError(player, rpName);
                }
            } else {
                event.getPlayer().getOpenInventory().close();
            }
            //}
        }
    }

    /**
     * If module SPECIAL_BLOCK_GET is enabled in world config file
     * opens the custom block inventory when a player droppes two stone blocks from the creative inventory.
     * @param event 
     */
    /*@EventHandler(priority=EventPriority.LOWEST)
    public void openSpecialInventory(InventoryCreativeEvent event) {
        if(PluginData.isModuleEnabled(event.getWhoClicked().getWorld(), Modules.SPECIAL_BLOCKS_GET)) {
            if(event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)
                    && event.getCursor().getType().equals(Material.STONE)
                    && event.getCursor().getAmount()==2) {
                if(!(event.getWhoClicked() instanceof Player)) {
                    return;
                }
                final Player p = (Player) event.getWhoClicked();
                String rpN = RpManager.getCurrentRpName(p);//PluginData.getRpName(ResourceRegionsUtil.getResourceRegionsUrl(p));
                if(rpN==null || rpN.equals("")) {
                    return; //+++rpN = "Gondor";
                }
                final String rpName = rpN;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.closeInventory();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(!SpecialBlockInventoryData.openInventory(p, rpName)) {
                                    sendNoInventoryError(p,rpName);
                                }  
                            }
                        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }*/

    @EventHandler
    public void selectItem(PlayerDropItemEvent event) {
        if((event.getPlayer().getOpenInventory().getTopInventory()) instanceof CraftInventoryCrafting // check if player has no open inventory (internal crafting inventory is returned in this case)
                && PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_GET)) {
            if (SpecialBlockInventoryData.getSpecialBlockDataFromItem(event.getItemDrop().getItemStack()) != null) {
                //event.setCancelled(true);
                final Player player = event.getPlayer();
                //ItemStack handItem = player.getInventory().getItemInMainHand();
                ItemStack droppedItem = event.getItemDrop().getItemStack();
                SpecialBlock base = SpecialBlockInventoryData.getSpecialBlockDataFromItem(droppedItem);
                if (droppedItem.getAmount() > 1) {
                    //open block collection if target block has one defined
                    //Logger.getLogger("InventoryListener").info(droppedItem.getType()+" "+droppedItem.getAmount());
                    if (base == null || !base.hasCollection()
                            || !SpecialBlockInventoryData.openInventory(player, droppedItem)) {
                        sendNoInventoryError(player, "");
                    }
                } else {
                    if (base != null && base.hasNextBlock()) {
                        base = base.getNextBlock();
                        /*nextItem.setAmount(2);
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                player.getInventory().setItemInMainHand(nextItem);
                            }
                        }.runTaskLater(ArchitectPlugin.getPluginInstance(),20);*/
                    }
                }
                ItemStack nextItem = SpecialBlockInventoryData.getItem(base);
                nextItem.setAmount(2);
                player.getInventory().setItemInMainHand(nextItem);
            } else {
                event.setCancelled(true);
            }
        }
        /*Logger.getLogger(InventoryListener.class.getSimpleName()).info("Drop event "+ event.getItemDrop().getItemStack().getAmount()
        +" "+(event.getPlayer().getOpenInventory())+" InventoryView "+((event.getPlayer().getOpenInventory().getTopInventory()) instanceof CraftInventoryCrafting)
        +"\n InventoryType"+(event.getPlayer().getOpenInventory().getType())
                +"\n Bottom Inventory"+(event.getPlayer().getOpenInventory().getBottomInventory())
        +"\n Top Inventory"+(event.getPlayer().getOpenInventory().getTopInventory()));*/
    }

    /**
     * If module INVENTORY_ACCESS is enabled in world config file
     * allowes or blocks opening of inventories as defined in world config file.
     * Possible configurations:
     * TRUE: Always allow to open this kind of inventory
     * FALSE: Never allow to open this kind of inventory
     * BUILDER: TheGaffer plugin build permission needed to open this kind of inventory
     * EXCEPTION: As BUILDER but additionally inventory can be opened in no physics exception areas only.
     * @param event 
     */
    @EventHandler(priority = EventPriority.LOWEST) 
    public void blockInventories(InventoryOpenEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.INVENTORY_ACCESS)
                || !(event.getPlayer() instanceof Player)) {
            return;
        }
        InventoryAccess access = PluginData.getInventoryAccess(event.getInventory());
        Player player = (Player)event.getPlayer();
        Location loc = event.getInventory().getLocation();
        switch(access) {
            case TRUE:
                return;
            case FALSE:
                event.setCancelled(true);
                return;
            case BUILDER:
                if(!PluginData.checkBuildPermissions((Player)event.getPlayer(), loc,
                                                Permission.INVENTORY_OPEN)) {
                    event.setCancelled(true);
                }
                return;
            case EXCEPTION:
                if(!PluginData.checkBuildPermissions((Player)event.getPlayer(),loc,
                                                Permission.INVENTORY_OPEN)
                        || !NoPhysicsData.hasNoPhysicsException(loc.getBlock())){
                    event.setCancelled(true);
                }
        }
        /*
        if (//event.getInventory().getType().equals(InventoryType.ANVIL)
          event.getInventory().getType().equals(InventoryType.BEACON) 
         || event.getInventory().getType().equals(InventoryType.BREWING) 
         || event.getInventory().getType().equals(InventoryType.DISPENSER) 
         || event.getInventory().getType().equals(InventoryType.HOPPER) 
         || event.getInventory().getHolder() instanceof ShulkerBox 
         || event.getInventory().getType().equals(InventoryType.DROPPER)){
            Block block = event.getInventory().getLocation().getBlock();
            if(!NoPhysicsData.hasNoPhysicsException(block)
                    || !PluginData.hasPermission((Player)event.getPlayer(),Permission.INVENTORY_OPEN)) {
                event.setCancelled(true);
            }
        }*/
    }

/*    static HashSet<String> found = new HashSet<>();
   
    @EventHandler
    public void moveInventoryItem(InventoryMoveItemEvent event) {
        Logger.getGlobal().info("moveInventory");
    }
    @EventHandler
    public void moveInventoryInteact(InventoryInteractEvent event) {
        Logger.getGlobal().info("interactInventory");
    }*/
    

    static void sendNoInventoryError(CommandSender p, String rp) {
        if(rp.equals("")) {
            PluginData.getMessageUtil().sendErrorMessage(p, "Custom inventory not found.");
        } else {
            PluginData.getMessageUtil().sendErrorMessage(p, "No custom inventory found for rp \""+rp+"\".");
        }
    }
       
}
