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
package com.mcmiddleearth.architect.specialBlockHandling.listener;

import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialBlockInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock;
import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.specialBlockHandling.MushroomBlocks;
import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockType;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockItemBlock;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockVanillaDoor;
import com.mcmiddleearth.pluginutil.EventUtil;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.ResourceRegionsUtil;
import com.mcmiddleearth.util.TheGafferUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockListener implements Listener{
 
    /**
     * Handles player interaction with dragon eggs if module DRAGON_EGG is enabled in 
     * world config file. Teleportation of the egg is blocked.
     * Players need creative mode, permission INTERACT_EGG and build permission from 
     * TheGaffer plugin to interact with a dragon egg.
     * @param event 
     */
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
                        && PluginData.checkBuildPermissions(p, event.getClickedBlock().getLocation(),
                                                       Permission.INTERACT_EGG))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * If module SPECIAL_BLOCK_PLACE is enabled in world config file
     * handles placement of blocks from the MCME custom inventories.
     * @param event 
     */
    @EventHandler
    public void placeSpecialBlock(PlayerInteractEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)
                || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || !event.getHand().equals(EquipmentSlot.HAND) 
                || event.getPlayer().getInventory().getItemInMainHand()==null
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)
                || !(event.getPlayer().getInventory().getItemInMainHand().hasItemMeta())) {
            return;
        }
        final Player player = event.getPlayer();
        final ItemStack handItem = player.getInventory().getItemInMainHand();
        SpecialBlock data = getSpecialBlockDataFromItem(handItem);
        if(data == null || data.getType().equals(SpecialBlockType.VANILLA)
                        || data.getType().equals(SpecialBlockType.DOOR_VANILLA)) {
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
        if(data==null) {
            PluginData.getMessageUtil().sendErrorMessage(player, "Special block data not found, item is probably outdated.");
            return;
        }
        Block blockPlace = event.getClickedBlock().getRelative(event.getBlockFace());
        if(!blockPlace.isEmpty() 
                && !blockPlace.getType().equals(Material.GRASS)
                && !blockPlace.getType().equals(Material.FIRE)
                && !blockPlace.getType().equals(Material.LAVA)
                && !blockPlace.getType().equals(Material.WATER)
                ) {
            return;
        }
        if(!TheGafferUtil.hasGafferPermission(player,blockPlace.getLocation())) {
//Logger.getGlobal().info("No Gaffer Permission");
            return;
        }
//Logger.getGlobal().info("Gaffer Permission ok");
//Logger.getGlobal().info(""+data.getType());
        data.placeBlock(blockPlace, event.getBlockFace(), player);
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
    
    private SpecialBlock getSpecialBlockDataFromItem(ItemStack handItem) {
        ItemMeta meta = handItem.getItemMeta();
        if(!(meta.hasLore() 
                && meta.getLore().size()>1 
                && meta.getLore().get(0).equals(SpecialBlockInventoryData.SPECIAL_BLOCK_TAG))) {
            return null;
        }
        return SpecialBlockInventoryData.getSpecialBlock(meta.getLore().get(1));
    }
    /**
     * If module SPECIAL_BLOCK_PLACE is enabled in world config file
     * prevents changes of item durability.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockChangeDurability(PlayerItemDamageEvent event) {
        if(PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)) {
                event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPressurePlate(EntityInteractEvent event) {
//Logger.getGlobal().info("EntityInteract");
        if(PluginData.isModuleEnabled(event.getBlock().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)) {
                event.setCancelled(true);
        }
    }
    
    
    
    /**
     * If module SPECIAL_BLOCK_PLACE is enabled in world config file
     * prevents creation of a double slab block when stacking two half slabs.
     * Instead a corresponding full block is placed.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGHEST) 
    public void avoidDoubleSlab(BlockPlaceEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)
                || !(event.getBlock().getType().equals(Material.STONE_SLAB)
                    || event.getBlock().getType().equals(Material.OAK_SLAB)
                    || event.getBlock().getType().equals(Material.SANDSTONE_SLAB)
                    || event.getBlock().getType().equals(Material.PURPUR_SLAB))) {
            return;
        }
        //TODO 1.13 still needed?
        /*final BlockState blockState = event.getBlock().getState();
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
        blockState.update(true, false);*/
    }
        
    @EventHandler(priority = EventPriority.LOWEST) 
    public void blockPlayerInteraction(PlayerInteractEvent event) { //used for item blocks
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.BLOCK_PLAYER_INTERACTION)
                || !(event.getPlayer() instanceof Player)) {
            return;
        }
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && PluginData.getNoInteraction(event.getClickedBlock())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST) //TODO make configurable
    public void blockVanillaOrientations(BlockPlaceEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)) {
            return;
        }
        if(event.getBlockPlaced().getType().equals(Material.PUMPKIN) 
                || event.getBlockPlaced().getType().equals(Material.END_PORTAL_FRAME) ) {
            BlockState state = event.getBlock().getState();
            state.setRawData((byte)0);
            state.update(true,false);
        }
    }
    
 /**   @EventHandler(priority = EventPriority.LOWEST) //TODO make configurable
    public void blockRedstoneInteraction(PlayerInteractEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)) {
            return;
        }
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && (event.getClickedBlock().getType().equals(Material.REDSTONE_COMPARATOR_OFF) 
                    || (event.getClickedBlock().getType().equals(Material.REDSTONE_COMPARATOR_ON) )
                    || (event.getClickedBlock().getType().equals(Material.DIODE_BLOCK_OFF) )
                    || (event.getClickedBlock().getType().equals(Material.DIODE_BLOCK_ON) ))) {
            event.setCancelled(true);
        }t
    }*/
    
    /**
     * If module SPECIAL_BLOCK_PLACE is enabled in world config file
     * prevents player from changing armor stands used for item blocks.
     * @param event 
     */
    @EventHandler(priority = EventPriority.LOWEST) 
    public void blockArmorAtItemBlocks(PlayerInteractAtEntityEvent event) { //used for item blocks
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)
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
    
    /**
     * If module SPECIAL_BLOCK_PLACE is enabled in world config file
     * handles removing of armor stands associated to an item block when the 
     * item block is removed.
     * @param event 
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) 
    public void removeItemBlockArmorStand(BlockBreakEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.SPECIAL_BLOCKS_PLACE)) {
            return;
        }
//Logger.getGlobal().info("remove Item Block"+event.getBlock().getX()+" "+event.getBlock().getY()+" "+event.getBlock().getZ());
        Location loc = new Location(event.getBlock().getWorld(), event.getBlock().getX()+0.5,
                                    event.getBlock().getY(), event.getBlock().getZ()+0.5);
        SpecialBlockItemBlock.removeArmorStands(loc);
    }
    
    
    /**
     * place powered doors for creative inventory door items
     */
    @EventHandler
    public void vanillaDoorPlace(BlockPlaceEvent event) {
        if(!PluginData.isModuleEnabled(event.getPlayer().getWorld(), Modules.USE_POWERED_DOORS)
                || !event.getHand().equals(EquipmentSlot.HAND) 
                || event.getPlayer().getInventory().getItemInMainHand()==null
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            return;
        }
        final Player player = event.getPlayer();
        if(!TheGafferUtil.hasGafferPermission(player,event.getBlockPlaced().getLocation())) {
            return;
        }
        ItemStack handItem = player.getInventory().getItemInMainHand();
//Logger.getGlobal().info("HandItem: "+handItem);        
        SpecialBlock data = getSpecialBlockDataFromItem(handItem);
//Logger.getGlobal().info("Data: "+data);
        if(data==null) {
            Material material=null;
            boolean powered = true;
            switch(handItem.getType()) {
                /*case ACACIA_DOOR_ITEM:
                    material = Material.ACACIA_DOOR;
                    break;
                case DARK_OAK_DOOR_ITEM:
                    material = Material.DARK_OAK_DOOR;
                    break;
                case JUNGLE_DOOR_ITEM:
                    material = Material.JUNGLE_DOOR;
                    break;
                case SPRUCE_DOOR_ITEM:
                    material = Material.SPRUCE_DOOR;
                    break;*/
                case BIRCH_DOOR:
                    //material = Material.BIRCH_DOOR;
                    powered = false;
                    break;
                /*case WOOD_DOOR:
                    material = Material.WOODEN_DOOR;
                    break;*/
                case IRON_DOOR:
                    //material = Material.IRON_DOOR_BLOCK;
                    powered = false;
                    break;
            }
            if(material==null) {
                return;
            }
            ConfigurationSection config = new MemoryConfiguration();
            config.set("blockMaterial", material.name());
            config.set("powered", powered);
            data = SpecialBlockVanillaDoor.loadFromConfig(config,"temp");
        }
        if(!(data instanceof SpecialBlockVanillaDoor)) {
            return;
        }
        boolean hingeRight = ((Door)event.getBlock().getRelative(BlockFace.UP)
                                                  .getState().getData()).getHinge();
/*Logger.getGlobal().info("hinge upper: "+((Door)event.getBlock().getRelative(BlockFace.UP)
                                                  .getState().getData()).getHinge());
Logger.getGlobal().info("blockid: "+event.getBlock().getRelative(BlockFace.UP).getType());
Logger.getGlobal().info("placeblockdata: "+event.getBlockPlaced().getData());
Logger.getGlobal().info("placeblockid: "+event.getBlockPlaced().getType());*/
        ((SpecialBlockVanillaDoor)data).placeBlock(event.getBlock(), BlockFace.SELF, 
                                                   player, hingeRight);
    }
    

    
/***********************************************************************************************
/* Methods for old special blocks like six sided logs. Will no longer be needed once there are *
/* custom inventories for all                                                                  *
/***********************************************************************************************/
    
    /**
     * If module SIX_SIDED_LOG is enabled in world config file
     * handles placing of six sided logs.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void logPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if (p.getItemInHand().getType().equals(Material.OAK_LOG)
                || p.getItemInHand().getType().equals(Material.JUNGLE_LOG)) {
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
                } else if(!TheGafferUtil.hasGafferPermission(p,event.getBlock().getLocation())) {
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
    
    /**
     * If module SIX_SIDED_LOG is enabled in world config file
     * handles placing of huge mushroom blocks.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void giantMushroomPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();

        if (p.getItemInHand().getType().equals(Material.BROWN_MUSHROOM_BLOCK)
                || p.getItemInHand().getType().equals(Material.RED_MUSHROOM_BLOCK)) {
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
                } else if(!TheGafferUtil.hasGafferPermission(p,event.getBlock().getLocation())) {
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
    
    /**
     * If module PISTON_EXTENSIONS is enabled in world config file
     * handles placing of piston extensions.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void pistonPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        if (p.getItemInHand().getType().equals(Material.PISTON)
                || p.getItemInHand().getType().equals(Material.PISTON)) {
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
                } else if(!TheGafferUtil.hasGafferPermission(p,event.getBlock().getLocation())) {
//                    PluginData.getMessageUtil().sendErrorMessage(p, 
//                            PluginData.getGafferProtectionMessage(p, event.getBlock().getLocation()));
                    return;
                }
                    float yaw = p.getLocation().getYaw();
                    float pitch = p.getLocation().getPitch();
                    byte data = getPistonOrFurnaceDat(yaw, pitch, p.getItemInHand().getType());
                    Block block = event.getBlock();
                    final BlockState blockState = block.getState();
                    blockState.setType(Material.PISTON_HEAD);
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

    /**
     * Determines the correct data value for a piston extension or furnace block 
     * depending on player orientation.
     * @param yaw of the player who wants to place the block
     * @param pitch of the player who wants to place the block
     * @param type piston base or sticky piston base
     * @return 
     */
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
        if(type.equals(Material.PISTON)) {
            dat+=8;
        }
        return dat;
    }
    
    /**
     * If module PLANTS is enabled in world config file
     * handles placing of pants independent of ground below.
     * If a block is clicked with a corresponding material in hand the data
     * value of the block is changed instead of placing a new block.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void vegPlace(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && (EventUtil.isMainHandEvent(event) 
                    || p.getItemInHand().getType().equals(Material.CACTUS)
                    || p.getItemInHand().getType().equals(Material.ROSE_RED)
                    || p.getItemInHand().getType().equals(Material.ROSE_BUSH)
                    || p.getItemInHand().getType().equals(Material.DEAD_BUSH)
                    || p.getItemInHand().getType().equals(Material.BROWN_MUSHROOM)
                    || p.getItemInHand().getType().equals(Material.RED_MUSHROOM)
                    || p.getItemInHand().getType().equals(Material.LILY_PAD))
                &&(p.getItemInHand().getType().equals(Material.CARROT)
                  || p.getItemInHand().getType().equals(Material.POTATO)
                  || p.getItemInHand().getType().equals(Material.WHEAT)
                  || p.getItemInHand().getType().equals(Material.MELON_SEEDS)
                  || p.getItemInHand().getType().equals(Material.PUMPKIN_SEEDS)
                  || p.getItemInHand().getType().equals(Material.BROWN_MUSHROOM)
                  || p.getItemInHand().getType().equals(Material.CACTUS)
                  || p.getItemInHand().getType().equals(Material.ROSE_RED)
                  || p.getItemInHand().getType().equals(Material.ROSE_BUSH)
                  || p.getItemInHand().getType().equals(Material.DEAD_BUSH)
                  || p.getItemInHand().getType().equals(Material.NETHER_WART)
                  || p.getItemInHand().getType().equals(Material.LILY_PAD)
                  || p.getItemInHand().getType().equals(Material.RED_MUSHROOM))) {
            if (!PluginData.isModuleEnabled(event.getClickedBlock().getWorld(), Modules.PLANTS)) {
                return;
            }
            if (p.getItemInHand().getItemMeta().hasDisplayName()
                    && p.getItemInHand().getItemMeta().getDisplayName().startsWith("Placeable")) {
                DevUtil.log(2,"vegPlace fired cancelled: " + event.isCancelled());
                if(event.isCancelled()) {
                    return;
                }
                if(!(PluginData.checkBuildPermissions(p, event.getClickedBlock().getLocation(),
                                                Permission.PLACE_PLANT))) {
                    event.setCancelled(true);
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
                                            Material.WHEAT, true, (byte)7);
                        break;
                    case MELON_SEEDS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.MELON_STEM, true, (byte)7);
                        break;
                    case PUMPKIN_SEEDS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.PUMPKIN_STEM, true, (byte)7);
                       break;
                    case CARROT:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(),  
                                            Material.CARROT, true, (byte)7);
                       break;
                    case POTATO:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.POTATO, true, (byte)7);
                        break;
                    case CACTUS:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.CACTUS, false,(byte)0);
                        break;
                    case NETHER_WART:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.NETHER_WART, true,(byte)3);
                        break;
                    case LILY_PAD:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.LILY_PAD, false,(byte)0);
                        break;
                    case ROSE_RED:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.ROSE_RED, false,p.getItemInHand().getData().getData());
                        break;
                    case ROSE_BUSH:
                        bs = handleInteract(event.getClickedBlock(), event.getBlockFace(), 
                                            Material.ROSE_BUSH, false,(byte) 0);
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
    
    /**
     * Handles placing and data value editing of plants. 
     * @param clickedBlock block the Player clicked at
     * @param clickedFace block Face the Placer clicked at, in this direction the new block will be placed
     * @param materialMatch If his material is found data value of that block is changed instead of placing a new block
     * @param editDataValue If editing of data value is allowed.
     * @param maxDataValue Maximum for data value.
     * @return 
     */
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

    /**
     * If module REDSTONE_TORCH is enabled in world config file
     * handles placing of redstone torches.
     * @param event 
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void torchPlace(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && EventUtil.isMainHandEvent(event)
                &&(p.getInventory().getItemInHand().getType().equals(Material.REDSTONE_TORCH))) {
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
                if(!(PluginData.checkBuildPermissions(p,event.getClickedBlock().getLocation(),
                                                 Permission.PLACE_TORCH))) {
                    return;
                }
                Block b = event.getClickedBlock().getRelative(event.getBlockFace());
                final BlockState bs = b.getState();
                if(bs.getType().equals(Material.AIR)) {
                    bs.setType(Material.REDSTONE_TORCH);
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
        if (p.getItemInHand().getType().equals(Material.STONE_SLAB)
                || p.getItemInHand().getType().equals(Material.OAK_SLAB)
                || p.getItemInHand().getType().equals(Material.SANDSTONE_SLAB)) {
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
                } else if(!TheGafferUtil.hasGafferPermission(p,event.getBlock().getLocation())) {
                    event.setCancelled(true);
//                    PluginData.getMessageUtil().sendErrorMessage(p, 
//                            PluginData.getGafferProtectionMessage(p, event.getBlock().getLocation()));
                    return;
                }
                Block b = event.getBlockPlaced();
                BlockState bs = b.getState();
                if(p.getItemInHand().getType().equals(Material.STONE_SLAB)) {
                    bs.setType(Material.STONE_SLAB);
                }
                else {
                    bs.setType(Material.SANDSTONE_SLAB);
                }
                if(p.getItemInHand().getType().equals(Material.OAK_SLAB)) {
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
                &&(p.getInventory().getItemInHand().getType().equals(Material.RED_BED))) {            
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
                if((PluginData.checkBuildPermissions(p, event.getClickedBlock().getLocation(),
                                                 Permission.PLACE_HALF_BED))) {
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
                    blockState.setType(Material.RED_BED);
                    blockState.setRawData(data);
                    blockState.update(true, false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            blockState.update(true, false);
                        }
                    }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
                }
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
                if((PluginData.checkBuildPermissions(p, event.getClickedBlock().getLocation(),
                                                Permission.PLACE_HALF_DOOR))) {
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

    @EventHandler(priority = EventPriority.HIGH)
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
                if(!(PluginData.checkBuildPermissions(p, event.getBlock().getLocation(), 
                                                      Permission.BURNING_FURNACE))) {
                    event.setCancelled(true);
                    return;
                }
                Furnace furnace = (Furnace) event.getBlock().getState();
                furnace.setType(Material.FURNACE);
                furnace.setRawData(getPistonOrFurnaceDat(p.getLocation().getYaw(),0,Material.FURNACE));
                furnace.update(true, false);
                final Block block = event.getBlock();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        final Furnace furnace = (Furnace) block.getState();
                        furnace.setBurnTime(Short.MAX_VALUE);
                        furnace.update(true, false); 
                        new BukkitRunnable() {
                            @Override
                            public void run(){
                                furnace.getInventory().setSmelting(new ItemStack(Material.COD));
                            }
                        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 10);
            }
        }
    }
    
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
    
    private byte getDoorDat(float yaw) {
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
    }
    
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
    
    private boolean isDoorItem(Material blockType) {
        return blockType.equals(Material.OAK_DOOR)
                || blockType.equals(Material.IRON_DOOR)
                || blockType.equals(Material.SPRUCE_DOOR)
                || blockType.equals(Material.BIRCH_DOOR)
                || blockType.equals(Material.JUNGLE_DOOR)
                || blockType.equals(Material.ACACIA_DOOR)
                || blockType.equals(Material.DARK_OAK_DOOR);
    }
    
    private Material doorItemToBlock(Material itemMaterial) {
        return itemMaterial;
        /* 1.13 removed
        switch(itemMaterial) {
            case WOOD_DOOR: return Material.WOODEN_DOOR;
            case IRON_DOOR: return Material.IRON_DOOR_BLOCK;
            case SPRUCE_DOOR_ITEM: return Material.SPRUCE_DOOR;                
            case BIRCH_DOOR_ITEM: return Material.BIRCH_DOOR;                
            case JUNGLE_DOOR_ITEM: return Material.JUNGLE_DOOR;                
            case ACACIA_DOOR_ITEM: return Material.ACACIA_DOOR;                
            case DARK_OAK_DOOR_ITEM: return Material.DARK_OAK_DOOR;                
        }
        return null;*/
    }

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
//END DELETE
    
    
}
