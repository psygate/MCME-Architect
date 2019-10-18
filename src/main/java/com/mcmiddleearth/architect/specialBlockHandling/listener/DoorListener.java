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
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialBlockInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockVanillaDoor;
import com.mcmiddleearth.architect.watcher.WatchedListener;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.DoorUtil;
import com.mcmiddleearth.util.TheGafferUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class DoorListener extends WatchedListener{
    
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
                    if(!DoorUtil.isUpperDoorBlock(event.getClickedBlock()) && !(DoorUtil.isDoor(above.getType()) && DoorUtil.isUpperDoorBlock(above))){
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
    @EventHandler(priority = EventPriority.LOW) 
    public void interactAdjacentDoors(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && event.getHand().equals(EquipmentSlot.HAND)
                && DoorUtil.isDoorBlock(event.getClickedBlock())) {
            Block block = event.getClickedBlock();
//Logger.getGlobal().info("Clicked block"+((Door)block.getBlockData()).getHinge().name());
            if(DoorUtil.isThinWall(block)) {
                return;
            }
            event.setCancelled(true);
            if(DoorUtil.isUpperDoorBlock(block)) {              //click at upper door part
                block = block.getRelative(BlockFace.DOWN);      //check if clicked block is part of full door
                if(!DoorUtil.isDoorBlock(block)) {
                    return;
                }
            } else {                                                           //click at lower door part
                if(!DoorUtil.isUpperDoorBlock(block.getRelative(BlockFace.UP))//check for half door
                    && !DoorUtil.isFullDoorAbove(block)) {                    //check for 3 block high door
                    return;
                }
            }
            if(DoorUtil.isFullDoorAbove(block)) {
                block = block.getRelative(BlockFace.UP);   //
            }
            boolean hingeRightOfClickedSide = ((Door)block.getRelative(BlockFace.UP)
                                               .getBlockData()).getHinge().equals(Door.Hinge.RIGHT);
//Logger.getGlobal().info("Lower block "+hingeRightOfClickedSide);
            DoorUtil.toggleDoor(block);
            DoorUtil.toggleDoor(block.getRelative(BlockFace.UP));
            DoorUtil.toggleHalfDoor(block.getRelative(BlockFace.DOWN),
                                    hingeRightOfClickedSide,
                                    ((Door)block.getState().getBlockData()).isOpen());

            Block block2Lower = DoorUtil.getSecondHalf(block);
            Block block2Upper = block2Lower.getRelative(BlockFace.UP);
            BlockState block2LowerState = block2Lower.getState();
            BlockState block2UpperState = block2Upper.getState();
            if(DoorUtil.isDoorBlock(block2Lower) && DoorUtil.isDoorBlock(block2Upper) 
                && (((Door)block2LowerState.getBlockData()).getFacing()
                            .equals(((Door)block.getBlockData()).getFacing())) //check for same facing
                && (((Door)block2UpperState.getBlockData()).getHinge()           //check for opposite hinge
                       !=(((Door)block.getRelative(BlockFace.UP).getBlockData()).getHinge()))) {
                DoorUtil.toggleDoor(block2Lower);
                DoorUtil.toggleDoor(block2Upper);
                DoorUtil.toggleHalfDoor(block2Lower.getRelative(BlockFace.DOWN),
                                        !hingeRightOfClickedSide,
                                        ((Door)block2Lower.getBlockData()).isOpen());
            }
        }
    }
    
    /**
     * place powered doors for creative inventory door items
     * @param event
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
        SpecialBlock data = SpecialBlockInventoryData.getSpecialBlockDataFromItem(handItem);
//Logger.getGlobal().info("Data: "+data);
        if(data==null) {
            Material material=null;
            boolean powered = true;
            switch(handItem.getType()) {
                case ACACIA_DOOR:
                    material = Material.ACACIA_DOOR;
                    break;
                case DARK_OAK_DOOR:
                    material = Material.DARK_OAK_DOOR;
                    break;
                case JUNGLE_DOOR:
                    material = Material.JUNGLE_DOOR;
                    break;
                case SPRUCE_DOOR:
                    material = Material.SPRUCE_DOOR;
                    break;
                case BIRCH_DOOR:
                    material = Material.BIRCH_DOOR;
                    powered = false;
                    break;
                case OAK_DOOR:
                    material = Material.OAK_DOOR;
                    break;
                case IRON_DOOR:
                    material = Material.IRON_DOOR;
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
//Logger.getGlobal().info("door data: "+data);
        }
        if(!(data instanceof SpecialBlockVanillaDoor)) {
            return;
        }
        boolean hingeRight = ((Door)event.getBlock().getRelative(BlockFace.UP)
                                                  .getBlockData()).getHinge().equals(Door.Hinge.RIGHT);
/*Logger.getGlobal().info("hinge upper: "+((Door)event.getBlock().getRelative(BlockFace.UP)
                                                  .getBlockData()).getHinge());
Logger.getGlobal().info("blockid: "+event.getBlock().getRelative(BlockFace.UP).getType());
Logger.getGlobal().info("placeblockdata: "+event.getBlockPlaced().getData());
Logger.getGlobal().info("placeblockid: "+event.getBlockPlaced().getType());*/
        ((SpecialBlockVanillaDoor)data).placeBlock(event.getBlock(), BlockFace.SELF, 
                                                   player, hingeRight);
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
    
    /*@EventHandler
    public void movePlayer(PlayerMoveEvent event) {
        Location loc = event.getFrom();
//Logger.getGlobal().info("From: "+loc.getBlockX()+" "+loc.getBlockZ());
        loc = event.getTo();
//Logger.getGlobal().info("To: "+loc.getBlockX()+" "+loc.getBlockZ());
    }
    private static Set<Player> asyncPlayers = new HashSet<>();
    
    public static void addOpenHalfDoorListener() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(ArchitectPlugin.getPluginInstance(),
                ListenerPriority.NORMAL, 
                PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.POSITION
                        || event.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
                    PacketContainer packet = event.getPacket();
                    StructureModifier<Double> position = packet.getDoubles();
                    Location loc = new Location(event.getPlayer().getWorld(),
                                                position.getValues().get(0),
                                                position.getValues().get(1),
                                                position.getValues().get(2));
//Logger.getGlobal().info("Position: "+loc.getBlockX()+" "+loc.getBlockZ());
                    if(event.getPacketType()==PacketType.Play.Client.POSITION) {
                        loc.setYaw(event.getPlayer().getLocation().getYaw());
                        loc.setPitch(event.getPlayer().getLocation().getPitch());
                    } else {
                        StructureModifier<Float> look = packet.getFloat();
                        loc.setYaw(look.getValues().get(0));
                        loc.setPitch(look.getValues().get(1));
                    }
                    Block block = loc.getBlock();
                    if(block.getTypeId()==64 && block.getData()==(byte)5 ) {
                        if(!asyncPlayers.contains(event.getPlayer())) {
//Logger.getGlobal().info("added");
                            asyncPlayers.add(event.getPlayer());
                        }
                    } else {
                        if(asyncPlayers.contains(event.getPlayer())) {
//Logger.getGlobal().info("removed");
                            asyncPlayers.remove(event.getPlayer());
                            event.getPlayer().teleport(loc);
                        }
                    }
                }
            }
        });          
        protocolManager.addPacketListener(
        new PacketAdapter(ArchitectPlugin.getPluginInstance(), ListenerPriority.NORMAL, 
                  PacketType.Play.Server.POSITION) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == 
                        PacketType.Play.Server.POSITION) {
                    Player player = event.getPlayer();
                    //PacketContainer packet = event.getPacket();
                    //Block door = player.getLocation().getBlock();
//Logger.getGlobal().info("is half door: "+DoorUtil.isDoorBlock(door));
                    if(asyncPlayers.contains(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }*/
}
