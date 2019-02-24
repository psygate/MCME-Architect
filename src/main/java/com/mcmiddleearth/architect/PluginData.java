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

import com.mcmiddleearth.pluginutil.FileUtil;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.TheGafferUtil;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    private static final Map<String,WorldConfig> worldConfigs = new HashMap<>();
    
    private static YamlConfiguration defaultWorldConfig = new YamlConfiguration();
    
    @Getter
    private static String defaultKey = "-default";
                                       
    //private static final Map<String, String> rpUrls = new HashMap<>();
    
    @Getter
    @Setter
    private static boolean overrideWeather = false;
    
    @Getter
    private static final MessageUtil messageUtil = new MessageUtil();
    
    @Getter
    private static final Set<UUID> afkPlayerList = new HashSet<>();
    
    @Getter
    private static int entityStandLimit = 1000;
    
    @Getter
    private static int entityLimitRadius = 80;
    
    private final static String ENITIY_LIMIT_SECTION = "EntityLimit";
    
    public static boolean isModuleEnabled(World world, Modules modul) {
        WorldConfig config = getOrCreateWorldConfig(world.getName());
        DevUtil.log(40, "isEnabled? "+modul.getModuleKey());
        return config.isModuleEnabled(modul,true);
    }

    public static void setModuleEnabled(World world, Modules modul, boolean enable) {
        if(world==null) {
            worldConfigs.get(defaultKey).setModuleEnabled(modul,enable);
        } else {
            getOrCreateWorldConfig(world.getName()).setModuleEnabled(modul, enable);
        }
    }

    public static WorldConfig getOrCreateWorldConfig(String worldName) {
        WorldConfig config = worldConfigs.get(worldName);
        if(config == null) {
            config = new WorldConfig(worldName, defaultWorldConfig);
            worldConfigs.put(worldName, config);
        }
        return config;
    }
    
    public static void load(){
        /*ConfigurationSection rpConfig = ArchitectPlugin.getPluginInstance().getConfig()
                                                       .getConfigurationSection("ServerResourcePacks");
        for(String rpKey: rpConfig.getKeys(false)) {
            rpUrls.put(rpKey, rpConfig.getString(rpKey));
        }*/
        ConfigurationSection entityConfig = ArchitectPlugin.getPluginInstance().getConfig()
                                                       .getConfigurationSection(ENITIY_LIMIT_SECTION);
        if(entityConfig==null) {
            entityConfig = ArchitectPlugin.getPluginInstance().getConfig().createSection(ENITIY_LIMIT_SECTION);
            entityConfig.set("number", 500);
            entityConfig.set("radius", 80);
            ArchitectPlugin.getPluginInstance().saveConfig();
        }
        entityStandLimit = entityConfig.getInt("number",500);
        entityLimitRadius = entityConfig.getInt("radius",80);
        worldConfigs.clear();
        WorldConfig config = new WorldConfig(WorldConfig.getDefaultWorldConfigName(), defaultWorldConfig);
        worldConfigs.put(defaultKey, config);
        defaultWorldConfig = config.getWorldConfig();
        File[] configFiles = WorldConfig.getWorldConfigDir().listFiles(FileUtil
                                        .getFileExtFilter(WorldConfig.getCfgExtension()));
        if(configFiles!=null) {
            for(File file: configFiles) {
                String worldName = FileUtil.getShortName(file);
                if(!worldName.equalsIgnoreCase(WorldConfig.getDefaultWorldConfigName())) {
                    getOrCreateWorldConfig(worldName);
                }
            }
        }
        for(World world: Bukkit.getWorlds()) {
            configureWorld(world);
        }
    }
    
    /*public static void save() throws IOException{
        for(WorldConfig config:worldConfigs.values()) {
            config.saveConfigFile();
        }
    }*/
    
    public static boolean hasPermission(CommandSender player, Permission perm) {
        return player.hasPermission(perm.getPermissionNode());
    }
    
    public static void configureWorld(World world) {
        getOrCreateWorldConfig(world.getName());
        boolean allowMonsters = true;
        boolean allowAnimals = true;
        if (PluginData.isModuleEnabled(world, Modules.ANIMAL_SPAWN_BLOCKING)) {
            allowAnimals = false;
        }
        if (PluginData.isModuleEnabled(world, Modules.MONSTER_SPAWN_BLOCKING)) {
            allowMonsters = false;
        }
        world.setSpawnFlags(allowMonsters, allowAnimals);    
        world.setGameRule(GameRule.DO_FIRE_TICK, true);
        if (PluginData.isModuleEnabled(world, Modules.FIRE_SPREAD_BLOCKING)) {
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
        }
        DevUtil.log(world.getName());
        DevUtil.log("fireTick "+world.getGameRuleValue(GameRule.DO_FIRE_TICK));
        DevUtil.log("animals "+world.getAllowAnimals());
        DevUtil.log("mobs "+world.getAllowMonsters());
        for(String rule: world.getGameRules()) {
            DevUtil.log(rule+" "+world.getGameRuleValue(rule));
        }
    }   
    
    public static InventoryAccess getInventoryAccess(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        Location loc = inventory.getLocation();
        if(loc == null) {
            return InventoryAccess.TRUE;
        }
        WorldConfig config = getOrCreateWorldConfig(loc.getWorld().getName());
        return config.getInventoryAccess(inventory);
    }
    
    public static boolean getNoInteraction(Block block) {
        WorldConfig config = getOrCreateWorldConfig(block.getWorld().getName());
        return config.getNoInteraction(block.getBlockData());
    }
    
    public static Set<String> getWorldNames() {
        Set<String> names = new HashSet<>();
        for(World world: Bukkit.getWorlds()) {
            names.add(world.getName());//getOrCreateWorldConfig(world.getName());
        }
        return names;//worldConfigs.keySet();
    }
    
    public static boolean setAFK(UUID player) {
        return afkPlayerList.add(player);
    }
    
    public static boolean isAFK(UUID player) {
        return afkPlayerList.contains(player);
    }
    
    public static boolean undoAFK(UUID player) {
        return afkPlayerList.remove(player);
    }
    
    /*public static String getRpUrl(String rpKey) {
        if(rpUrls.containsKey(rpKey)) {
            return rpUrls.get(rpKey);
            }
        return "";
    }
    
    public static String getRpName(String rpUrl) {
        for(String search: rpUrls.keySet()) {
            if(rpUrls.get(search).equalsIgnoreCase(rpUrl)) {
                return search;
            }
        }
        return "";
    }
    
    public static String matchRpName(String rpKey) {
        for(String search: rpUrls.keySet()) {
            if(search.toLowerCase().startsWith(rpKey.toLowerCase())) {
                return search;
            }
        }
        return "";
    }*/
    
    public static boolean moreEntitiesAllowed(Block block) {
        Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation(), 
                                                                            entityLimitRadius, 
                                                                            200, 
                                                                            entityLimitRadius);
        return entities.size()<entityStandLimit;
    }
    
    public static int countNearbyEntities(Block block) {
        Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation(), 
                                                                            entityLimitRadius, 
                                                                            200, 
                                                                            entityLimitRadius);
        return entities.size();
    }
    
    public static boolean checkBuildPermissions(Player player, Location loc, Permission perm) {
        if(!hasPermission(player,perm)) {
            getMessageUtil().sendNoPermissionError(player);
            return false;
        } 
        return TheGafferUtil.checkGafferPermission(player,loc);
   }
}
