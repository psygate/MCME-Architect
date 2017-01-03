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
package com.mcmiddleearth.architect.specialBlockHandling;

import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialBlockInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock;
import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsData;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockItemBlock;
import com.mcmiddleearth.pluginutil.EventUtil;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.ResourceRegionsUtil;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockListener implements Listener{
 
    @EventHandler(priority = EventPriority.HIGH)
    private void eggInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.hasBlock() 
                && EventUtil.isMainHandEvent(event)
                && event.getClickedBlock().getType().equals(Material.DRAGON_EGG)) {
            DevUtil.log(2,"eggInteract fired cancelled: " + event.isCancelled());
            if(event.isCancelled()) {
                return;
            }
            if(!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.DRAGON_EGG)) {
                return;
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                event.setCancelled(true);
            }
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (!(p.getGameMode().equals(GameMode.CREATIVE)
                        && PluginData.hasPermission(p, Permission.INTERACT_EGG))) {
                    event.setCancelled(true);
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                } else if(!PluginData.hasGafferPermission(p,event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                    PluginData.getMessageUtil().sendErrorMessage(p, 
                            PluginData.getGafferProtectionMessage(p, event.getClickedBlock().getLocation()));
                }
            }
        }
    }

/*    @EventHandler(priority = EventPriority.HIGH)
    private void logPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if (p.getItemInHand().getType().equals(Material.LOG)
                || p.getItemInHand().getType().equals(Material.LOG_2)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.SIX_SIDED_LOGS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Six Sided")) {
                DevUtil.log(2,"logPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!(PluginData.hasPermission(p, Permission.PLACE_SIX_SIDED_LOG))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p); 
                    event.setCancelled(true);
                    return;
                } else if(!PluginData.hasGafferPermission(p,event.getBlock().getLocation())) {
                    event.setCancelled(true);
//                    PluginData.getMessageUtil().sendErrorMessage(p, 
//                            PluginData.getGafferProtectionMessage(p, event.getBlock().getLocation()));
                    return;
                }
                Block b = event.getBlockPlaced();
                MaterialData md = p.getItemInHand().getData();
                md.setData((byte) (md.getData()+12));
                BlockState bs = b.getState();

                bs.setData(md);
                bs.update();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void giantMushroomPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if (p.getItemInHand().getType().equals(Material.HUGE_MUSHROOM_1)
                || p.getItemInHand().getType().equals(Material.HUGE_MUSHROOM_2)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.SIX_SIDED_LOGS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasEnchants()) {
                DevUtil.log(2,"giantMushroomPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!(PluginData.hasPermission(p, Permission.PLACE_SIX_SIDED_LOG))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                } else if(!PluginData.hasGafferPermission(p,event.getBlock().getLocation())) {
                    event.setCancelled(true);
//                    PluginData.getMessageUtil().sendErrorMessage(p, 
//                            PluginData.getGafferProtectionMessage(p, event.getBlock().getLocation()));
                    return;
                }
                Block b = event.getBlockPlaced();
                MaterialData md = p.getItemInHand().getData();
                md.setData(MushroomBlocks.getDataValue(p.getItemInHand().getItemMeta().getDisplayName()));
                final BlockState bs = b.getState();
                bs.setData(md);
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        bs.update(true, false);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void pistonPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand().getType().equals(Material.PISTON_BASE)
                || p.getItemInHand().getType().equals(Material.PISTON_STICKY_BASE)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.PISTON_EXTENSIONS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName() && 
                (p.getItemInHand().getItemMeta().getDisplayName().startsWith("Table")
                || p.getItemInHand().getItemMeta().getDisplayName().startsWith("Wheel"))) {
                    DevUtil.log(2,"pistonPlace fired cancelled: " + event.isCancelled());
                    if(event.isCancelled()) {
                        return;
                    }
                    event.setCancelled(true);
                    if(!(PluginData.hasPermission(p, Permission.PLACE_PISTON_EXTENSION))) {
                        PluginData.getMessageUtil().sendNoPermissionError(p);
                        return;
                } else if(!PluginData.hasGafferPermission(p,event.getBlock().getLocation())) {
//                    PluginData.getMessageUtil().sendErrorMessage(p, 
//                            PluginData.getGafferProtectionMessage(p, event.getBlock().getLocation()));
                    return;
                }
                    float yaw = p.getLocation().getYaw();
                    float pitch = p.getLocation().getPitch();
                    byte data = getPistonOrFurnaceDat(yaw, pitch, p.getItemInHand().getType());
                    Block block = event.getBlock();
                    final BlockState blockState = block.getState();
                    blockState.setType(Material.PISTON_EXTENSION);
                    blockState.setRawData(data);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            blockState.update(true, false);
                        }
                    }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }

    private static byte getPistonOrFurnaceDat(float yaw, float pitch, Material type) {
        byte dat = 0;
        while(yaw>180) yaw-=360;
        while(yaw<-180) yaw+=360;
        if (pitch  < -45) {
            dat = 0;
        } else if (pitch  > 45) {
            dat = 1;
        } else if ((yaw >= -45 && yaw < 45)) {
            dat = 2;
        } else if (yaw < -135 || yaw >= 135) {
            dat = 3;
        } else if ((yaw >= -135 && yaw < -45)) {
            dat = 4;
        } else if ((yaw >= 45 && yaw < 135)) {
            dat = 5;
        }
        if(type.equals(Material.PISTON_STICKY_BASE)) {
            dat+=8;
        }
        return dat;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void vegPlace(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && (EventUtil.isMainHandEvent(event) 
                    || p.getItemInHand().getType().equals(Material.CACTUS)
                    || p.getItemInHand().getType().equals(Material.RED_ROSE)
                    || p.getItemInHand().getType().equals(Material.YELLOW_FLOWER)
                    || p.getItemInHand().getType().equals(Material.DEAD_BUSH)
                    || p.getItemInHand().getType().equals(Material.BROWN_MUSHROOM)
                    || p.getItemInHand().getType().equals(Material.RED_MUSHROOM)
                    || p.getItemInHand().getType().equals(Material.WATER_LILY))
                &&(p.getItemInHand().getType().equals(Material.CARROT_ITEM)
                  || p.getItemInHand().getType().equals(Material.POTATO_ITEM)
                  || p.getItemInHand().getType().equals(Material.WHEAT)
                  || p.getItemInHand().getType().equals(Material.MELON_SEEDS)
                  || p.getItemInHand().getType().equals(Material.PUMPKIN_SEEDS)
                  || p.getItemInHand().getType().equals(Material.BROWN_MUSHROOM)
                  || p.getItemInHand().getType().equals(Material.CACTUS)
                  || p.getItemInHand().getType().equals(Material.RED_ROSE)
                  || p.getItemInHand().getType().equals(Material.YELLOW_FLOWER)
                  || p.getItemInHand().getType().equals(Material.DEAD_BUSH)
                  || p.getItemInHand().getType().equals(Material.NETHER_STALK)
                  || p.getItemInHand().getType().equals(Material.WATER_LILY)
                  || p.getItemInHand().getType().equals(Material.RED_MUSHROOM))) {
            if (!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.PLANTS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Placeable")) {
                DevUtil.log(2,"vegPlace fired cancelled: " + event.isCancelled());
                /*if(event.isCancelled()) {
                    return;
                }*/
             /*   if(!(PluginData.hasPermission(p, Permission.PLACE_PLANT))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                } else if(!PluginData.hasGafferPermission(p,event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                    PluginData.getMessageUtil().sendErrorMessage(p, 
                            PluginData.getGafferProtectionMessage(p, event.getClickedBlock().getLocation()));
                    return;
                }
                Block b = event.getClickedBlock().getRelative(event.getBlockFace());
                BlockState bs = b.getState();
                switch(p.getItemInHand().getType()) {
                    case BROWN_MUSHROOM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.BROWN_MUSHROOM, true, (byte)0);
                        break;
                    case RED_MUSHROOM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.RED_MUSHROOM, true, (byte)0);
                        break;
                    case WHEAT:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.CROPS, true, (byte)7);
                        break;
                    case MELON_SEEDS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.MELON_STEM, true, (byte)7);
                        break;
                    case PUMPKIN_SEEDS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.PUMPKIN_STEM, true, (byte)7);
                       break;
                    case CARROT_ITEM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(),  
                                            Material.CARROT, true, (byte)7);
                       break;
                    case POTATO_ITEM:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.POTATO, true, (byte)7);
                        break;
                    case CACTUS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.CACTUS, false,(byte)0);
                        break;
                    case NETHER_STALK:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.NETHER_WARTS, true,(byte)3);
                        break;
                    case WATER_LILY:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.WATER_LILY, false,(byte)0);
                        break;
                    case RED_ROSE:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.RED_ROSE, false,p.getItemInHand().getData().getData());
                        break;
                    case YELLOW_FLOWER:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.YELLOW_FLOWER, false,(byte) 0);
                        break;
                    case DEAD_BUSH:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.DEAD_BUSH, false,(byte) 0);
                        break;

                }
                bs.update(true,false);
            }
        }
    }
    
    private static BlockState handleInteract(Block clickedBlock,BlockFace clickedFace, 
                                             Material materialMatch, boolean editDataValue, byte maxDataValue) {
        BlockState blockState;
        if(clickedBlock.getType().equals(materialMatch) ) {
            blockState = clickedBlock.getState();
            if(editDataValue) {
                byte dataValue = (byte) (blockState.getRawData()- 1);
                if(dataValue<0) {
                    dataValue = maxDataValue;
                }
                blockState.setRawData(dataValue);
            }
        }
        else {
            blockState = clickedBlock.getRelative(clickedFace).getState();
            if(blockState.getType().equals(Material.AIR)) {
                blockState.setType(materialMatch);
                blockState.setRawData(maxDataValue);
            }
        }
        return blockState;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void torchPlace(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && EventUtil.isMainHandEvent(event)
                &&(p.getInventory().getItemInHand().getType().equals(Material.REDSTONE_TORCH_ON))) {
            if (!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.REDSTONE_TORCH)) {
                return;
            }
            if (p.getInventory().getItemInHand().getItemMeta().hasDisplayName()
                    && p.getInventory().getItemInHand().getItemMeta().getDisplayName().startsWith("Unlit")) {
                DevUtil.log(2,"torchPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                event.setCancelled(true);
                if(!(PluginData.hasPermission(p, Permission.PLACE_TORCH))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    return;
                } else if(!PluginData.hasGafferPermission(p,event.getClickedBlock().getLocation())) {
                    PluginData.getMessageUtil().sendErrorMessage(p, 
                            PluginData.getGafferProtectionMessage(p, event.getClickedBlock().getLocation()));
                    return;
                }
                Block b = event.getClickedBlock().getRelative(event.getBlockFace());
                final BlockState bs = b.getState();
                if(bs.getType().equals(Material.AIR)) {
                    bs.setType(Material.REDSTONE_TORCH_OFF);
                    bs.setRawData(getTorchDat(event.getBlockFace()));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            bs.update(true, false);
                        }
                    }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
                }
            }
        }
    }
    
    private byte getTorchDat(BlockFace face) {
        switch(face) {
            case NORTH:
                return (byte) 4;
            case SOUTH:
                return (byte) 3;
            case WEST:
                return (byte) 2;
            case EAST:
                return (byte) 1;
            default:
                return (byte) 0;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void doubleSlabPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand().getType().equals(Material.STEP)
                || p.getItemInHand().getType().equals(Material.WOOD_STEP)
                || p.getItemInHand().getType().equals(Material.STONE_SLAB2)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.DOUBLE_SLABS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Double")) {
                DevUtil.log(2,"doubleSlabPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!(PluginData.hasPermission(p, Permission.PLACE_DOUBLE_SLAB))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                } else if(!PluginData.hasGafferPermission(p,event.getBlock().getLocation())) {
                    event.setCancelled(true);
//                    PluginData.getMessageUtil().sendErrorMessage(p, 
//                            PluginData.getGafferProtectionMessage(p, event.getBlock().getLocation()));
                    return;
                }
                Block b = event.getBlockPlaced();
                BlockState bs = b.getState();
                if(p.getItemInHand().getType().equals(Material.STONE_SLAB2)) {
                    bs.setType(Material.DOUBLE_STONE_SLAB2);
                }
                else {
                    bs.setType(Material.DOUBLE_STEP);
                }
                if(p.getItemInHand().getType().equals(Material.WOOD_STEP)) {
                    bs.setRawData((byte) 2);
                }
                else if(p.getItemInHand().getItemMeta().getDisplayName().startsWith("Double Full")) {
                    bs.setRawData((byte) 8);
                }
                else {
                    bs.setRawData(p.getItemInHand().getData().getData());
                }
                bs.update(true,false);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    private void bedPlace(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.hasBlock()
                && EventUtil.isMainHandEvent(event)
                &&(p.getInventory().getItemInHand().getType().equals(Material.BED))) {            
            if (!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.HALF_BEDS)) {
                return;
            }
            ItemStack item = p.getItemInHand();
            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                  && item.getItemMeta().getDisplayName().startsWith("Half")) {
                DevUtil.log(2,"bedPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                event.setCancelled(true);
                Block block = event.getClickedBlock().getRelative(event.getBlockFace());
                final BlockState blockState = block.getState();
                //final BlockState upperBlockState = block.getRelative(0, 1, 0).getState();
                if(!(PluginData.hasPermission(p, Permission.PLACE_HALF_BED))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                } else if(!PluginData.hasGafferPermission(p,event.getClickedBlock().getLocation())) {
                    PluginData.getMessageUtil().sendErrorMessage(p, 
                            PluginData.getGafferProtectionMessage(p, event.getClickedBlock().getLocation()));
                } else {
                    float yaw = p.getLocation().getYaw();
                    byte data = (byte)(getDoorDat(yaw)-1);
                    if(data<0) {
                        data=3;
                    }
                    if(item.getItemMeta().getDisplayName().endsWith("(head)")){
                        data = (byte) (data+6);
                    }
                    if(data<8) {
                        data = (byte) (data+4);
                    }
                    blockState.setType(Material.BED_BLOCK);
                    blockState.setRawData(data);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        blockState.update(true, false);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void doorPlace(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.hasBlock()
                && EventUtil.isMainHandEvent(event)
                &&(isDoorItem(p.getInventory().getItemInHand().getType()))) {            
            if (!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.HALF_DOORS)) {
                return;
            }
            ItemStack item = p.getItemInHand();
            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                  && item.getItemMeta().getDisplayName().startsWith("Half")) {
                DevUtil.log(2,"doorPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                event.setCancelled(true);
                Block block = event.getClickedBlock().getRelative(event.getBlockFace());
                final BlockState blockState = block.getState();
                final BlockState upperBlockState = block.getRelative(0, 1, 0).getState();
                if(!(PluginData.hasPermission(p, Permission.PLACE_HALF_DOOR))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                } else if(!PluginData.hasGafferPermission(p,event.getClickedBlock().getLocation())) {
                    PluginData.getMessageUtil().sendErrorMessage(p, 
                            PluginData.getGafferProtectionMessage(p, event.getClickedBlock().getLocation()));
                } else {
                    float yaw = p.getLocation().getYaw();
                    byte data = getDoorDat(yaw);

                    blockState.setType(doorItemToBlock(item.getType()));
                    blockState.setRawData(data);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        blockState.update(true, false);
                        upperBlockState.update(true, false);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
            }
        }
    }

/*   @EventHandler(priority = EventPriority.HIGH)
    private void doorPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Material blockType = event.getBlock().getType();
Logger.getGlobal().info("1");
        if (isDoor(blockType)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.HALF_DOORS)) {
                return;
            }
Logger.getGlobal().info("2");
            ItemStack item = p.getItemInHand();
Logger.getGlobal().info("3");
            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                  && item.getItemMeta().getDisplayName().startsWith("Half")) {
Logger.getGlobal().info("4");
                DevUtil.log(2,"doorPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                event.setCancelled(true);
                /*if(!(PluginData.hasPermission(p, Permission.PLACE_HALF_DOOR))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    return;
                } else if(!PluginData.hasGafferPermission(p,event.getBlock().getLocation())) {
                    PluginData.getMessageUtil().sendErrorMessage(p, 
                            PluginData.getGafferProtectionMessage(p, event.getBlock().getLocation()));
                    return;
                }
                float yaw = p.getLocation().getYaw();
                byte data = getDoorDat(yaw);

                Block block = event.getBlock();
                final BlockState blockState = block.getState();
                blockState.setRawData(data);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        blockState.update(true, false);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 5);
            }
        }
    }*/

    @EventHandler
    public void furnaceProlongBurning(FurnaceSmeltEvent event) {
        if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BURNING_FURNACE)) {
            return;
        }
        final Block block = event.getBlock();
        final Material smelting = ((Furnace) block.getState()).getInventory().getSmelting().getType();
        new BukkitRunnable() {
            @Override
            public void run() {
                Furnace furnace = (Furnace) block.getState();
                FurnaceInventory inventory = furnace.getInventory();
                ItemStack item =inventory.getResult();
                inventory.setResult(null);
                inventory.setSmelting(new ItemStack(smelting));
                furnace.setBurnTime(Short.MAX_VALUE);
                furnace.update(true, false);
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
    }
    
    /*@EventHandler(priority = EventPriority.HIGH)
    public void placeFurnace(BlockPlaceEvent event) {
        final Player p = event.getPlayer();
        if(event.getBlock().getType().equals(Material.FURNACE)) {
            if (!PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.BURNING_FURNACE)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Burning")) {
                DevUtil.log(2,"placeBurningFurnace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!(PluginData.hasPermission(p, Permission.BURNING_FURNACE))) {
                    PluginData.getMessageUtil().sendNoPermissionError(p);
                    event.setCancelled(true);
                    return;
                } else if(!PluginData.hasGafferPermission(p,event.getBlock().getLocation())) {
                    event.setCancelled(true);
                    return;
                }
                Furnace furnace = (Furnace) event.getBlock().getState();
                furnace.setType(Material.BURNING_FURNACE);
                furnace.setRawData(getPistonOrFurnaceDat(p.getLocation().getYaw(),0,Material.BURNING_FURNACE));
                furnace.update(true, false);
                final Block block = event.getBlock();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Furnace furnace = (Furnace) block.getState();
                        furnace.getInventory().setSmelting(new ItemStack(Material.RAW_FISH));
                        furnace.setBurnTime(Short.MAX_VALUE);
                        furnace.update(true, false); 
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 10);
            }
        }
    }*/
    
    /*@EventHandler(priority = EventPriority.HIGH)
    public void protectFurnaceInventory(InventoryOpenEvent event) {
        HumanEntity player = event.getPlayer();
        if(!(player instanceof Player)) {
            return;
        }
        Player p = (Player) player;
        if(event.getInventory() instanceof FurnaceInventory) {
            if (!PluginData.isModuleEnabled(p.getWorld(), Modules.BURNING_FURNACE)) {
                return;
            }
            DevUtil.log(2,"protectInventoryBurningFurnace fired cancelled: " + event.isCancelled());
            if(event.isCancelled()) {
                return;
            }
            if(!(PluginData.hasPermission(p, Permission.BURNING_FURNACE)
                        && PluginData.hasGafferPermission(p,p.getLocation()))) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                event.setCancelled(true);
            }
        }
    }*/
    
    /*private byte getDoorDat(float yaw) {
        if ((yaw >= -225 && yaw < -135)
                || (yaw >= 135 && yaw <= 225)) {
            return 3;
        } else if ((yaw >= -135 && yaw < -45)
                || (yaw >= 225 && yaw < 315)) {
            return 0;
        } else if ((yaw >= -45 && yaw < 45)
                || (yaw >= -360 && yaw < -315)
                || (yaw >= 315 && yaw <= 360)) {
            return 1;
        } else if ((yaw >= -315 && yaw < -225)
                || (yaw >= 45 && yaw < 135)) {
            return 2;
        } else {
            return 0;
        }
    }*/
    
    /*
    @EventHandler(priority = EventPriority.HIGH)
    private void noPhysicsDoors(BlockPhysicsEvent event) {
        Material doorType = event.getBlock().getType();
        if (isDoor(doorType)) {
            DevUtil.log(2,"noPhysicsDoors fired cancelled: " + event.isCancelled());
            if(event.isCancelled()) {
                return;
            }
            if(PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.HALF_DOORS)) {
                event.setCancelled(true);
            }
        }
    }*/
    
    @EventHandler(priority = EventPriority.HIGH)
    private void noOpenHalfDoors(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player p = event.getPlayer();
            Material blockType = event.getClickedBlock().getType();
            if (isDoor(blockType)) {
                if(PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.HALF_DOORS)) {
                    DevUtil.log(2,"noOpenHalfDoors fired cancelled: " + event.isCancelled());
                    if(event.isCancelled()) {
                        return;
                    }
                    Block above = event.getClickedBlock().getRelative(BlockFace.UP);
                    if(!isUpperDoorPart(event.getClickedBlock()) && !(isDoor(above.getType()) && isUpperDoorPart(above))){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    private boolean isUpperDoorPart(Block block) { //doesnt'work for powered doors
        return block.getData()==8 || block.getData()==9; 
    }

    private boolean isDoor(Material blockType) {
        return blockType.equals(Material.WOODEN_DOOR)
                || blockType.equals(Material.IRON_DOOR_BLOCK)
                || blockType.equals(Material.SPRUCE_DOOR)
                || blockType.equals(Material.BIRCH_DOOR)
                || blockType.equals(Material.JUNGLE_DOOR)
                || blockType.equals(Material.ACACIA_DOOR)
                || blockType.equals(Material.DARK_OAK_DOOR);
    }

    /*private boolean isDoorItem(Material blockType) {
        return blockType.equals(Material.WOOD_DOOR)
                || blockType.equals(Material.IRON_DOOR)
                || blockType.equals(Material.SPRUCE_DOOR_ITEM)
                || blockType.equals(Material.BIRCH_DOOR_ITEM)
                || blockType.equals(Material.JUNGLE_DOOR_ITEM)
                || blockType.equals(Material.ACACIA_DOOR_ITEM)
                || blockType.equals(Material.DARK_OAK_DOOR_ITEM);
    }
    
    private Material doorItemToBlock(Material itemMaterial) {
        switch(itemMaterial) {
            case WOOD_DOOR: return Material.WOODEN_DOOR;
            case IRON_DOOR: return Material.IRON_DOOR_BLOCK;
            case SPRUCE_DOOR_ITEM: return Material.SPRUCE_DOOR;                
            case BIRCH_DOOR_ITEM: return Material.BIRCH_DOOR;                
            case JUNGLE_DOOR_ITEM: return Material.JUNGLE_DOOR;                
            case ACACIA_DOOR_ITEM: return Material.ACACIA_DOOR;                
            case DARK_OAK_DOOR_ITEM: return Material.DARK_OAK_DOOR;                
        }
        return null;
    }*/

    /*@EventHandler
    public void cycleDurability(PlayerInteractEvent event) {
        if(PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.ITEM_TEXTURES)) {
            if(event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                ItemStack item = event.getItem();
                if(item.getDurability()>0) {
                    item.setDurability((short) (item.getDurability()-1));
                }
            } else  if(event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                ItemStack item = event.getItem();
                item.setDurability((short) (item.getDurability()+1));
            }
        }
    }*/
    
    @EventHandler
    public void placeSpecialBlock(PlayerInteractEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)
                || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || event.getPlayer().getInventory().getItemInMainHand()==null
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)
                || !(event.getPlayer().getInventory().getItemInMainHand().hasItemMeta())) {
            return;
        }
        final Player player = event.getPlayer();
        final ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta meta = handItem.getItemMeta();
        if(!(meta.hasLore() 
                && meta.getLore().size()>1 
                && meta.getLore().get(0).equals(SpecialBlockInventoryData.SPECIAL_BLOCK_TAG))) {
            return;
        }
        event.setCancelled(true); //cancel Event for main and off hand to avoid perks plugin removing the item
        final ItemStack[] armor = player.getInventory().getArmorContents();
        final ItemStack offHandItem = player.getInventory().getItemInOffHand();
        new BukkitRunnable() {
            @Override
            public void run() {
                ((PlayerInventory)player.getInventory()).setArmorContents(armor);
                player.getInventory().setItemInMainHand(handItem);
                player.getInventory().setItemInOffHand(offHandItem);
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
        SpecialBlock data = SpecialBlockInventoryData.getSpecialBlock(meta.getLore().get(1));
        if(data==null) {
            PluginData.getMessageUtil().sendErrorMessage(player, "Special block data not found, item is probably outdated.");
            return;
        }
        Block blockPlace = event.getClickedBlock().getRelative(event.getBlockFace());
        if(!blockPlace.isEmpty() && !blockPlace.getType().equals(Material.LONG_GRASS)) {
            return;
        }
        if(!PluginData.hasGafferPermission(player,blockPlace.getLocation())) {
            return;
        }
        data.placeBlock(blockPlace, event.getBlockFace(), player.getLocation());
/*Logger.getGlobal().info("specialBlock place 7 main");
        } else {
Logger.getGlobal().info("specialBlock place 7 off");
/*ArrayList<HandlerList> list = HandlerList.getHandlerLists();
for(HandlerList handlerList : list) {
    RegisteredListener reg = new RegisteredListener(this, new EventExecutor(){
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            String name = event.getEventName();
            if(!found.contains(name)) {
                found.add(name);
Logger.getGlobal().info("Event found: "+event.getEventName());
            }
        }
    },EventPriority.NORMAL,ArchitectPlugin.getPluginInstance(),false);
    handlerList.register(reg);
    //RegisteredListener[] listener = handlerList.getRegisteredListeners();
  //  for(int i=0; i<listener.length;i++) {
//Logger.getGlobal().info(listener[i].getListener().toString());
    //}
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
    
    @EventHandler(priority=EventPriority.LOWEST)  
    public void openSpecialInventory(PlayerSwapHandItemsEvent event) {
        if(PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)) {
            event.setCancelled(true);
            final Player p = (Player) event.getPlayer();
            String rpN = PluginData.getRpName(ResourceRegionsUtil.getResourceRegionsUrl(p));
            if(rpN==null || rpN.equals("")) {
                rpN = "Gondor";
            }
            SpecialBlockInventoryData.openInventory(p, rpN);
        }
    }
    
    
    @EventHandler(priority=EventPriority.LOWEST) 
    public void openSpecialInventory(InventoryCreativeEvent event) {
        if(event.getSlotType().equals(SlotType.OUTSIDE)
                && event.getCursor().getType().equals(Material.STONE)
                && event.getCursor().getAmount()==2) {
            if(!(event.getWhoClicked() instanceof Player)) {
                return;
            }
            final Player p = (Player) event.getWhoClicked();
            String rpN = PluginData.getRpName(ResourceRegionsUtil.getResourceRegionsUrl(p));
            if(rpN==null || rpN.equals("")) {
                rpN = "Gondor";
            }
            final String rpName = rpN;
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.closeInventory();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            SpecialBlockInventoryData.openInventory(p, rpName);  
                        }
                    }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
                }
            }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void changeDurability(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST) 
    public void avoidDoubleSlab(BlockPlaceEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)
                || !(event.getBlock().getType().equals(Material.DOUBLE_STEP)
                    || event.getBlock().getType().equals(Material.DOUBLE_STONE_SLAB2)
                    || event.getBlock().getType().equals(Material.WOOD_DOUBLE_STEP)
                    || event.getBlock().getType().equals(Material.PURPUR_DOUBLE_SLAB))) {
            return;
        }
        final BlockState blockState = event.getBlock().getState();
        switch(blockState.getType()) {
            case DOUBLE_STEP:
                switch(blockState.getRawData()) {
                    case 1:
                        blockState.setType(Material.SANDSTONE);
                        blockState.setRawData((byte)0);
                        break;
                     case 2:
                        blockState.setType(Material.WOOD);
                        blockState.setRawData((byte)0);
                        break;
                    case 3:
                        blockState.setType(Material.COBBLESTONE);
                        blockState.setRawData((byte)0);
                        break;
                    case 4:
                        blockState.setType(Material.BRICK);
                        blockState.setRawData((byte)0);
                        break;
                    case 5:
                        blockState.setType(Material.SMOOTH_BRICK);
                        blockState.setRawData((byte)0);
                        break;
                    case 6:
                        blockState.setType(Material.NETHER_BRICK);
                        blockState.setRawData((byte)0);
                        break;
                    case 7:
                        blockState.setType(Material.QUARTZ_BLOCK);
                        blockState.setRawData((byte)0);
                        break;
                }
                break;
            case DOUBLE_STONE_SLAB2:
                blockState.setType(Material.RED_SANDSTONE);
                break;
            case WOOD_DOUBLE_STEP:
                byte dv = blockState.getRawData();
                blockState.setType(Material.WOOD);
                blockState.setRawData(dv);
                break;
            case PURPUR_DOUBLE_SLAB:
                blockState.setType(Material.PURPUR_BLOCK);
                break;
        }
        blockState.update(true, false);
    }
    
    
    @EventHandler(priority = EventPriority.LOWEST) 
    public void blockInventories(InventoryOpenEvent event) {
Logger.getGlobal().info("open inventory "+event.getInventory().getType().name());
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)
                || !(event.getPlayer() instanceof Player)) {
            return;
        }
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
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST) 
    public void blockPoweredAcaciaFenceGate(PlayerInteractEvent event) { //used for item blocks
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)
                || !(event.getPlayer() instanceof Player)) {
            return;
        }
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getClickedBlock().getType().equals(Material.ACACIA_FENCE_GATE)
                && event.getClickedBlock().getData()>7) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST) 
    public void blockArmorAtItemBlocks(PlayerInteractAtEntityEvent event) { //used for item blocks
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)
                || !(event.getPlayer() instanceof Player)) {
            return;
        }
        if(event.getRightClicked() instanceof ArmorStand
                && ((ArmorStand)event.getRightClicked()).getCustomName()!=null
                && ((ArmorStand)event.getRightClicked()).getCustomName()
                                     .startsWith(SpecialBlockItemBlock.PREFIX)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST) 
    public void blockVanillaOrientations(BlockPlaceEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)) {
            return;
        }
        if(event.getBlockPlaced().getType().equals(Material.PUMPKIN) 
                || event.getBlockPlaced().getType().equals(Material.ENDER_PORTAL_FRAME) ) {
            event.getBlock().setData((byte)0);
        }
    }
    
    @EventHandler
    public void removeItemBlockArmorStand(BlockBreakEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)) {
            return;
        }
        Location loc = new Location(event.getBlock().getWorld(), event.getBlock().getX()+0.5,
                                    event.getBlock().getY()-1, event.getBlock().getZ()+0.5);
        for(Entity entity: event.getBlock().getWorld().getNearbyEntities(loc, 0.5, 2, 0.5)) {
            if(entity.getCustomName()!=null
                    && entity.getCustomName().equals("iBE_"+event.getBlock().getX()+"_"+event.getBlock().getY() 
                                             +"_"+event.getBlock().getZ())) {
                entity.remove();
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
    
    @EventHandler(priority = EventPriority.LOWEST) 
    public void interactAdjacentDoors(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && isDoorBlock(event.getClickedBlock())) {
            Block block = event.getClickedBlock();
            if(isThinWall(block)) {
//Logger.getGlobal().info("interact Door at thin wall");
                return;
            }
            event.setCancelled(true);
            if(isUpperDoorBlock(block)) {                       //click at upper door part
                block = block.getRelative(BlockFace.DOWN);      //check if clicked block is part of full door
                if(!isDoorBlock(block)) {
                    return;
                }
            } else {                                            //click at lower door part
                if(!isDoorBlock(block.getRelative(BlockFace.UP))//check for half door
                    && !isFullDoorBelow(block)) {               //check for 3 block high door
                    return;
                }
            }
            if(isFullDoorBelow(block)) {
                block = block.getRelative(BlockFace.DOWN, 2);   //
            }
            toggleDoor(block);
            toggleDoor(block.getRelative(BlockFace.UP));
            toggleDoor(block.getRelative(BlockFace.UP,2));

            Block block2Lower = getSecondHalf(block);
            Block block2Upper = block2Lower.getRelative(BlockFace.UP);
            BlockState block2LowerState = block2Lower.getState();
            BlockState block2UpperState = block2Upper.getState();
            if(isDoorBlock(block2Lower) && isDoorBlock(block2Upper) 
                && (((Door)block2LowerState.getData()).getFacing()
                            .equals(((Door)block.getState().getData()).getFacing())) //check for same facing
                && (((Door)block2UpperState.getData()).getHinge()           //check for opposite hinge
                       !=(((Door)block.getRelative(BlockFace.UP).getState().getData()).getHinge()))) {
                toggleDoor(block2Lower);
                toggleDoor(block2Upper);
                toggleDoor(block2Lower.getRelative(BlockFace.UP,2));
            }
        }
    }

    private boolean isThinWall(Block block) {
/*Logger.getGlobal().info("***********isThinWall**********");
Logger.getGlobal().info(""+isDoorBlock(block));
Logger.getGlobal().info(""+isUpperDoorPart(block));
Logger.getGlobal().info(""+(block.getData()>9));
Logger.getGlobal().info(""+block.getType().name());*/
        if(isUpperDoorBlock(block)) {
            return isDoorBlock(block) && (block.getType().equals(Material.BIRCH_DOOR) 
                                          || block.getType().equals(Material.JUNGLE_DOOR))
                                      && block.getData()>9;                                //check powered state
        } else {
            block = block.getRelative(BlockFace.UP);
            return isUpperDoorBlock(block) && (block.getType().equals(Material.BIRCH_DOOR) 
                                               || block.getType().equals(Material.JUNGLE_DOOR))
                                           && block.getData()>9;                                //check powered state
        }
    }
    private boolean isUpperDoorBlock(Block block) {
        return isDoorBlock(block)
              && ((Door)block.getState().getData()).isTopHalf();
    }
    
    private boolean isLowerDoorBlock(Block block) {
        return  isDoorBlock(block)
                && !isUpperDoorBlock(block);
    }
    
    private boolean isDoorBlock(Block block) {
        return block.getState().getData() instanceof Door;
    }
    
    private boolean isFullDoorBelow(Block block) {
        return   isUpperDoorBlock(block.getRelative(BlockFace.DOWN))
                && isLowerDoorBlock(block.getRelative(BlockFace.DOWN,2));
    }
    
    private Block getSecondHalf(Block block) {
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
    
    private void toggleDoor(Block block) {
        if(block.getState().getData() instanceof Door
              && !((Door)block.getState().getData()).isTopHalf()
              && !(isThinWall(block))) {
//Logger.getGlobal().info("toggle "+block.getX()+" "+block.getY()+" "+block.getZ());
            BlockState state = block.getState();
//Logger.getGlobal().info("dv "+state.getRawData());
            Door data = (Door) state.getData();
            data.setOpen(!data.isOpen());
            state.setData(data);
            state.update();
//Logger.getGlobal().info("dv new "+state.getRawData());
        }
    }
    
    private void closeDoor(Block block) {
        block.setData((byte)(block.getData()-4), false);
    }
    
    private void openDoor(Block block) {
        block.setData((byte)(block.getData()+4), false);
    }
    
    
    /*@EventHandler(priority = EventPriority.LOWEST) 
    public void blockRedstoneOreChange(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.LEFT_CLICK_AIR) 
                || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        if(event.getClickedBlock().getType().equals(Material.REDSTONE_ORE)) {
            event.setCancelled(true);
        }
    }*/
    
    /*@EventHandler(priority = EventPriority.LOWEST) 
    public void avoidDecayingLeaves(PlayerInteractEvent event) {
        event.
    }
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)
                || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || !(event.getClickedBlock().getType().equals(Material.LEAVES)
                     || event.getClickedBlock().getType().equals(Material.LEAVES_2))) {
            return;
        }
        final BlockState clickedState=event.getClickedBlock().getState();
        /*if(clickedState.getRawData()<4) {
            clickedState.setRawData((byte)(clickedState.getRawData()+4));
        }
        while(clickedState.)*/
        /*final BlockState state = event.getClickedBlock().getState();
        new BukkitRunnable() {
            @Override
            public void run() {
                state.update(true, false);
Logger.getGlobal().info("reset "+state.getRawData()+" "+state.getX()+" "+state.getY()+" "+state.getZ());
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
    }    
    /*
    @EventHandler(priority = EventPriority.MONITOR) 
    public void avoidDecayingLeaves(BlockPlaceEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS)
                || !(event.getPlayer().getItemInHand().getType().equals(Material.LEAVES)
                     || event.getPlayer().getItemInHand().getType().equals(Material.LEAVES_2))) {
            return;
        }
        event.setCancelled(true);
        //final BlockState state = event.getBlock().getState();
        /*new BukkitRunnable() {
            @Override
            public void run() {
                state.update(true, false);
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);*/
    //}    
    
    
    
}
