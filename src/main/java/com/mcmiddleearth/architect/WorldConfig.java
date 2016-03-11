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
package com.mcmiddleearth.architect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Eriol_Eandur
 */
public class WorldConfig {
    
    @Getter
    private static final File worldConfigDir = new File(ArchitectPlugin.getPluginInstance().getDataFolder()
                                                       +File.separator+"WorldConfig");
    
    @Getter
    private static final String cfgExtension = "yml";
    
    @Getter
    private static final String defaultWorldConfigName = "defaultWorldConfig";
    
    private static final File defaultConfig = new File(worldConfigDir+File.separator
                                                       +defaultWorldConfigName+"."+cfgExtension);
    
    private static final String NO_PHYSICS_LIST = "noPhysicsList";
    
    private final List<Integer> npList;
    
    private final String worldName;
    
    private YamlConfiguration config;

    public WorldConfig(String worldName){
        if(!worldConfigDir.exists()) {
            worldConfigDir.mkdirs();
        }
        this.worldName = worldName;
        File configFile = new File(worldConfigDir+File.separator+worldName+"."+cfgExtension);
        if(configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
            npList = config.getIntegerList(NO_PHYSICS_LIST);
        } else { 
            if(defaultConfig.exists()) {
                config = YamlConfiguration.loadConfiguration(defaultConfig);
            }
            else {
                config = new YamlConfiguration();
                for(Modules modul : Modules.values()) {
                    config.set(modul.getModuleKey(), true);
                }
                config.set(NO_PHYSICS_LIST, new ArrayList<Integer>());
                try {
                    config.save(defaultConfig);
                } catch (IOException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            npList = config.getIntegerList(NO_PHYSICS_LIST);
            saveConfigFile();
        }
    }
    
    public final void saveConfigFile(){
        config.set(NO_PHYSICS_LIST, npList);
        File file = new File(worldConfigDir+"/"+worldName+"."+cfgExtension);
        try {
            config.save(file);
        } catch (IOException ex) {
            Logger.getLogger(WorldConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isModuleEnabled(Modules module, boolean defaultValue) {
        if(!config.contains(module.getModuleKey())) {
            config.set(module.getModuleKey(), defaultValue);
            saveConfigFile();
        }
        return config.getBoolean(module.getModuleKey());
    }
    
    public boolean isNoPhysicsBlock(int typeId) {
        return npList.contains(typeId);
    }
    
    public String getNpListAsString() {
        String result="";
        Integer[] npArray = npList.toArray(new Integer[0]);
       Arrays.sort(npArray);
        for(Integer i:npArray) {
            result = result + i + " ";
        }
        return result;
    }

    public boolean addToNpList(int blockId) {
        if(!isNoPhysicsBlock(blockId)) {
            npList.add(blockId);
            saveConfigFile();
            return true;
        }
        return false;
    }
    
    public boolean removeFromNpList(int blockId) {
        if(isNoPhysicsBlock(blockId)) {
            npList.remove(npList.indexOf(blockId));
            saveConfigFile();
            return true;
        }
        return false;
    }
 }
