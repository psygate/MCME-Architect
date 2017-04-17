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

import com.mcmiddleearth.pluginutil.NumericUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

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
    
    private static final String INVENTORY_ACCESS = "inventoryAccess";
    
    private static final String NO_INTERACTION = "noInteraction";
    
    private final List<Integer> npList;
    
    private final String worldName;
    
    private YamlConfiguration config;

    static {
        if(!worldConfigDir.exists()) {
            worldConfigDir.mkdirs();
        }
    }
    
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
                createInventoryAccess();
                createNoInteraction();
                try {
                    config.save(defaultConfig);
                } catch (IOException ex) {
                    Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            npList = config.getIntegerList(NO_PHYSICS_LIST);
            saveConfigFile();
        }
        if(config.getConfigurationSection(INVENTORY_ACCESS)==null) {
            createInventoryAccess();
            saveConfigFile();
        }
        if(config.getConfigurationSection(NO_INTERACTION)==null) {
            createNoInteraction();
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
    
    public InventoryAccess getInventoryAccess(Inventory inventory) {
        ConfigurationSection section = config.getConfigurationSection(INVENTORY_ACCESS);
        String key=section.getString(inventory.getType().name());
        if(key==null) {
            section.set(inventory.getType().name(),InventoryAccess.TRUE);
            saveConfigFile();
            return InventoryAccess.TRUE;
        }
        return InventoryAccess.valueOf(key);
    }
    
    private void createInventoryAccess() {
        ConfigurationSection section = config.createSection(INVENTORY_ACCESS);
        section.set(InventoryType.ANVIL.name(), InventoryAccess.TRUE.name());
        section.set(InventoryType.BEACON.name(), InventoryAccess.EXCEPTION.name());
        section.set(InventoryType.BREWING.name(), InventoryAccess.BUILDER.name());
        section.set(InventoryType.CHEST.name(), InventoryAccess.TRUE.name());
        section.set(InventoryType.CRAFTING.name(), InventoryAccess.TRUE.name());
        section.set(InventoryType.CREATIVE.name(), InventoryAccess.TRUE.name());
        section.set(InventoryType.DISPENSER.name(), InventoryAccess.EXCEPTION.name());
        section.set(InventoryType.DROPPER.name(), InventoryAccess.EXCEPTION.name());
        section.set(InventoryType.ENCHANTING.name(), InventoryAccess.EXCEPTION.name());
        section.set(InventoryType.ENDER_CHEST.name(), InventoryAccess.TRUE.name());
        section.set(InventoryType.FURNACE.name(), InventoryAccess.BUILDER.name());
        section.set(InventoryType.HOPPER.name(), InventoryAccess.EXCEPTION.name());
        section.set(InventoryType.PLAYER.name(), InventoryAccess.TRUE.name());
        section.set(InventoryType.WORKBENCH.name(), InventoryAccess.TRUE.name());
        section.set(InventoryType.MERCHANT.name(), InventoryAccess.TRUE.name());
        section.set("SHULKER_BOX", InventoryAccess.EXCEPTION.name());
    }
    
    public boolean getNoInteraction(BlockState state) {
        ConfigurationSection section = config.getConfigurationSection(NO_INTERACTION);
        String data = section.getString(state.getType().name());
        if(data!=null) {
            String[] values = data.split(";");
            for(String value: values) {
                int first= -1, last=-1;
                if(value.contains("-") && value.lastIndexOf("-")+1<value.length()) {
                    String firstPart = value.substring(0,value.indexOf("-"));
                    String lastPart = value.substring(value.lastIndexOf("-")+1);
                    if(NumericUtil.isInt(firstPart)) {
                        first = NumericUtil.getInt(firstPart);
                    }
                    if(NumericUtil.isInt(lastPart)) {
                        last = NumericUtil.getInt(lastPart);
                    } else {
                        last = first;
                    }
                } else {
                    if(NumericUtil.isInt(value)) {
                        first = NumericUtil.getInt(value);
                        last = first;
                    }
                }
                if(state.getRawData()>=first && state.getRawData()<=last) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    private void createNoInteraction() {
        ConfigurationSection section = config.createSection(NO_INTERACTION);
        section.set(Material.FENCE_GATE.name(), "8-15");
        section.set(Material.SPRUCE_FENCE_GATE.name(), "0-15");
        section.set(Material.BIRCH_FENCE_GATE.name(), "8-15");
        section.set(Material.JUNGLE_FENCE_GATE.name(), "0-15");
        section.set(Material.ACACIA_FENCE_GATE.name(), "0-15");
        section.set(Material.DARK_OAK_FENCE_GATE.name(), "0-15");
        section.set(Material.ACACIA_FENCE_GATE.name(), "0-15");
        section.set(Material.WOODEN_DOOR.name(), "10-11");
        section.set(Material.JUNGLE_DOOR.name(), "10-11");
        section.set(Material.SPRUCE_DOOR.name(), "10-11");
        section.set(Material.ACACIA_DOOR.name(), "10-11");
        section.set(Material.DARK_OAK_DOOR.name(), "10-11");
    }
 }
