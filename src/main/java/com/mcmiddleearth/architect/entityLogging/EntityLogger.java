/*
 * Copyright (C) 2020 Eriol_Eandur
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
package com.mcmiddleearth.architect.entityLogging;

import com.mcmiddleearth.architect.ArchitectPlugin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Eriol_Eandur
 */
public class EntityLogger{
    
    private static BukkitTask loggerTask;
    
    private static Map<Integer[],Integer[]> logData = new HashMap<>();
    
    private static File logFile = new File(ArchitectPlugin.getPluginInstance().getDataFolder(),"entityLog.dat");
    
    private static Listener listener; 
    
    public static void setLogging(boolean on) {
        if(on) {
            listener = new ELogListener();
            Bukkit.getPluginManager().registerEvents(listener, ArchitectPlugin.getPluginInstance());
            loggerTask = new BukkitRunnable() {
                @Override
                public void run() {
                    try(PrintWriter fw = new PrintWriter(new FileWriter(logFile))) {
                        logData.forEach((coord,value)->fw.println(""+coord[0]+";"+coord[1]+";"+value));
                    } catch (IOException ex) {
                        Logger.getLogger(EntityLogger.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.runTaskTimerAsynchronously(ArchitectPlugin.getPluginInstance(), 2000, 2000);
        } else {
            loggerTask.cancel();
            HandlerList.unregisterAll(listener);
        }
    }
    
    public static class ELogListener implements Listener {
        
        @EventHandler
        public void onChunkLoad(ChunkLoadEvent event) {
            Integer[] coords = new Integer[2];
            Chunk chunk = event.getChunk();
            coords[0] = chunk.getX() * 16;
            coords[1] = chunk.getZ() *16;
            Entity[] entities = chunk.getEntities();
            Integer armorStands = 0;
            for(Entity entity: entities) {
                if(entity instanceof ArmorStand) {
                    armorStands++;
                }
            }
            logData.put(coords, new Integer[]{armorStands,entities.length});
        }
    }
}
