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
package com.mcmiddleearth.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.reflect.StructureModifier;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class ProtocolLibUtil {
  
    private static ProtocolLibConnection connector;
    
    private static JavaPlugin pluginInstance;
    
    private static class ProtocolLibConnection {
        
        private ProtocolManager protocolManager = null;
        private PacketConstructor chunkBulkPacketConstructor = null;
        private PacketConstructor entityPacketConstructor = null;
    
        public ProtocolLibConnection() {
            try{
                protocolManager = ProtocolLibrary.getProtocolManager();
                List<Chunk> chunkList = new ArrayList<>();
                chunkBulkPacketConstructor = protocolManager.createPacketConstructor(PacketType.Play.Server.MAP_CHUNK_BULK, 
                                                                                      chunkList);
/*                int entityId = 1;
                protocolManager.addPacketListener(new PacketAdapter(pluginInstance,
                                                                    ListenerPriority.NORMAL, 
                                                                    PacketType.Play.Server.MAP_CHUNK_BULK) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK_BULK) {
                            //protocolManager.removePacketListeners(AutoTeleportPlugin.getPluginInstance());
                            DevUtil.log(2,"Found Map Chunk Bulk package");
                            PacketContainer packet = event.getPacket();
                            StructureModifier<int[]> ints = packet.getIntegerArrays();
                            DevUtil.log(2,"x "+arrayToString(ints.read(0)));
                            DevUtil.log(2,"y "+arrayToString(ints.read(1)));
                        }
                    }
                }); */
            }
            catch(NoClassDefFoundError e) {
                Logger.getLogger(ProtocolLibUtil.class.getName()).log(Level.WARNING, "ProtocolLib is missing.");
                protocolManager = null;
                chunkBulkPacketConstructor=null;
                return;
            }
        }

        private String arrayToString(int[] ints) {
            String result = "";
            for(int i:ints) {
                result= result+" "+i;
            }
            return result;
        }

        public void sendChunks(Player player, Collection<Chunk> chunkList) {
            if(chunkBulkPacketConstructor==null) {
                return;
            }
            PacketContainer chunkPacket = chunkBulkPacketConstructor.createPacket(chunkList);
            try {
                protocolManager.sendServerPacket(player, chunkPacket);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot send packet " + chunkPacket, e);
            }
        }
        
        public void sendEntityPacket(Player player, Chunk chunk) {
            Entity[] entityList = chunk.getEntities();
            List<Player> playerList = new ArrayList<>();
            playerList.add(player);
            for(Entity entity:entityList) {
                protocolManager.updateEntity(entity, playerList);
            }
        }
        
        public void _old_sendEntityPacket(Player player, Chunk chunk) {
            Entity[] entityList = chunk.getEntities();
            for(Entity entity:entityList) {
                int type = getType(entity);
                if(type>0) {
                    int data = getData(entity);
                    PacketConstructor lEntityPacketConstructor = protocolManager.createPacketConstructor(PacketType.Play.Server.SPAWN_ENTITY, 
                                                                                      entity, type, data);
                PacketContainer packet = lEntityPacketConstructor.createPacket(entity, type, data);
                try {
                    if(entity!=player) {
                    protocolManager.sendServerPacket(player, packet);
                    }
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException("Cannot send packet " + packet, e);
                    }
                }
            }
        }
        
        private int getType(Entity entity) {
            if(entity instanceof ArmorStand) {
                return 78;
            }
            else if(entity instanceof ItemFrame) {
                return 71;
            }
            return -1;
        }
        
        private int getData(Entity entity) {
            if(entity instanceof ItemFrame) {
                switch(((ItemFrame)entity).getFacing()) {
                    case SOUTH:
                        return 0;
                    case WEST:
                        return 1;
                    case NORTH:
                        return 2;
                    case EAST:
                        return 3;
                }
            }
            return 0;
        }
        
        public void _teleport_sendEntityPacket(Player player, Chunk chunk) {
            Entity[] entityList = chunk.getEntities();
            for(Entity entity:entityList) {
                //entityPacketConstructor.createPacket(entity.getEntityId());
                PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
                packet.getBytes()
                        .write(0, (byte)  new Float(entity.getLocation().getYaw()/360f*256).byteValue())
                        .write(1, (byte) new Float(entity.getLocation().getPitch()/360f*256).byteValue());
                packet.getIntegers()
                        .write(0, entity.getEntityId())
                        .write(1, new Double(entity.getLocation().getX()*32+32).intValue())
                        .write(2, new Double(entity.getLocation().getY()*32).intValue())
                        .write(3, new Double(entity.getLocation().getZ()*32+32).intValue());
                packet.getBooleans()
                        .write(0, entity.isOnGround());
            try {
                if(entity!=player) {
                protocolManager.sendServerPacket(player, packet);
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot send packet " + packet, e);
            }
            }
        }
    }
    
    public static void init(JavaPlugin plugin) {
        if(Bukkit.getPluginManager().getPlugin("ProtocolLib")!=null) {
            pluginInstance = plugin;
            connector = new ProtocolLibConnection();
        }
        else {
            Logger.getLogger(ProtocolLibUtil.class.getName()).log(Level.WARNING, "ProtocolLib plugin is missing.");
        }
    }
    
    public static void sendChunks(Player player, Collection<Chunk> chunkList) {
        if(isInitiated()) {
            connector.sendChunks(player, chunkList);
        }
    }
    
    public static void sendEntityPacket(Player player, Chunk chunk) {
        if(isInitiated()) {
            connector.sendEntityPacket(player, chunk);
        }
    }
    
    public static boolean isInitiated() {
        return connector != null;
    }

}
