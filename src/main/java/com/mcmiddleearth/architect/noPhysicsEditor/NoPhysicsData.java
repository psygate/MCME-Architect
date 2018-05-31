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
package com.mcmiddleearth.architect.noPhysicsEditor;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class NoPhysicsData {
    
    @Getter
    private static final Map<String, ExceptionArea> exceptionAreas = new HashMap<>();
    
    private static final File dataFile = new File(ArchitectPlugin.getPluginInstance().getDataFolder(),
                                                "NoPhyExceptionAreas.txt");
    
    public static boolean hasNoPhysicsException(Block block) {
        for(ExceptionArea area: exceptionAreas.values()) {
            if(area.isAffected(block.getType()) && area.isInside(block.getLocation())) {
                return true;
            }
        }
        return false;
    }
    
    public static void setExceptionArea(String name, CuboidRegion region, String type) {
        if(type.equalsIgnoreCase("redstone")) {
            exceptionAreas.put(name, new RedstoneCircuitArea(region));
        } else {
            exceptionAreas.put(name, new WaterFlowArea(region));
        }
    }
    
    public static void deleteExceptionArea(String name) {
        exceptionAreas.remove(name);
    }
    
    public static boolean exceptionAreaExists(String name) {
        return exceptionAreas.containsKey(name);
    }
    
    public static void save() throws IOException {
        File temp = new File(dataFile.getAbsoluteFile()+".tmp");
        if(temp.exists()) {
            temp.delete();
        }
        try (FileWriter fw = new FileWriter(temp);
            PrintWriter writer = new PrintWriter(fw)) {
            for(String name: exceptionAreas.keySet()) {
                ExceptionArea area = exceptionAreas.get(name);
                writer.println(area.getX()+";"+area.getY()+";"+area.getZ()+";"
                        +area.getDX()+";"+area.getDY()+";"+area.getDZ()+";"
                        +area.getWorldUID()+";"+name);
            }
        }
        if(dataFile.exists()) {
            dataFile.delete();
        }
        temp.renameTo(dataFile);
    }
    
    public static void load() {
        try (Scanner scanner = new Scanner(dataFile)) {
            scanner.useDelimiter(";");
            while(scanner.hasNext()) {
                int minX = scanner.nextInt();
                int minY = scanner.nextInt();
                int minZ = scanner.nextInt();
                int maxX = minX+scanner.nextInt();
                int maxY = minY+scanner.nextInt();
                int maxZ = minZ+scanner.nextInt();
                Vector minPoint = new Vector(minX,minY,minZ);
                Vector maxPoint = new Vector(maxX,maxY,maxZ);
                UUID world = UUID.fromString(scanner.next());
                String name = scanner.nextLine().substring(1);
//Logger.getGlobal().info("loadnophy "+name+" "+world);
                if(world==null) continue;
                exceptionAreas.put(name,new RedstoneCircuitArea(world,minPoint,maxPoint));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NoPhysicsData.class.getName()).log(Level.WARNING,"No physics exception data file not found.");
        }
    }
}
