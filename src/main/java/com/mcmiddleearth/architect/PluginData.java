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
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    private static final Map<String,WorldConfig> worldConfigs = new HashMap<>();
    
    private static final Map<String, String> rpUrls = new HashMap<>();
    
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
        WorldConfig config = worldConfigs.get(world.getName());
        if(config == null) {
            config = new WorldConfig(world.getName());
            worldConfigs.put(world.getName(), config);
        }
        DevUtil.log(40, "isEnabled? "+modul.getModuleKey());
        return config.isModuleEnabled(modul,true);
    }
    
    public static void load(){
        ConfigurationSection rpConfig = ArchitectPlugin.getPluginInstance().getConfig()
                                                       .getConfigurationSection("ServerResourcePacks");
        for(String rpKey: rpConfig.getKeys(false)) {
            rpUrls.put(rpKey, rpConfig.getString(rpKey));
        }
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
        File[] configFiles = WorldConfig.getWorldConfigDir().listFiles(FileUtil
                                        .getFileExtFilter(WorldConfig.getCfgExtension()));
        for(File file: configFiles) {
            String worldName = FileUtil.getShortName(file);
            if(!worldName.equalsIgnoreCase(WorldConfig.getDefaultWorldConfigName())) {
                worldConfigs.put(worldName, new WorldConfig(worldName));
            }
        }
        for(World world: Bukkit.getWorlds()) {
            configureWorld(world);
        }
    }
    
    public static void save() throws IOException{
        for(WorldConfig config:worldConfigs.values()) {
            config.saveConfigFile();
        }
    }
    
    public static boolean hasPermission(Player player, Permission perm) {
        return player.hasPermission(perm.getPermissionNode());
    }
    
    public static void configureWorld(World world) {
        boolean allowMonsters = true;
        boolean allowAnimals = true;
        if (PluginData.isModuleEnabled(world, Modules.ANIMAL_SPAWN_BLOCKING)) {
            allowAnimals = false;
        }
        if (PluginData.isModuleEnabled(world, Modules.MONSTER_SPAWN_BLOCKING)) {
            allowMonsters = false;
        }
        world.setSpawnFlags(allowMonsters, allowAnimals);    
        world.setGameRuleValue("doFireTick", true+"");
        if (PluginData.isModuleEnabled(world, Modules.FIRE_SPREAD_BLOCKING)) {
            world.setGameRuleValue("doFireTick", false+"");
        }
        DevUtil.log(world.getName());
        DevUtil.log("fireTick "+world.getGameRuleValue("doFireTick"));
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
        return worldConfigs.get(loc.getWorld().getName())
                           .getInventoryAccess(inventory);
    }
    
    public static boolean getNoInteraction(Block block) {
        return worldConfigs.get(block.getLocation().getWorld().getName())
                           .getNoInteraction(block.getState());
    }
    
    public static boolean isNoPhysicsBlock(Block block) {
        return worldConfigs.get(block.getWorld().getName()).isNoPhysicsBlock(block.getTypeId());
    }
    
    public static String getNpList(String worldName) {
        return worldConfigs.get(worldName).getNpListAsString();
    }

    public static boolean addNpBlock(String worldName, int blockId) {
        return worldConfigs.get(worldName).addToNpList(blockId);
    }
    
    public static boolean removeNpBlock(String worldName, int blockId) {
        return worldConfigs.get(worldName).removeFromNpList(blockId);
    }
    
    public static Set<String> getWorldNames() {
        return worldConfigs.keySet();
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
    
    public static boolean hasGafferPermission(Player player, Location location) {
        Plugin theGaffer = Bukkit.getPluginManager().getPlugin("TheGaffer");
        if(theGaffer == null) {
            return true;
        } else {
            try {
                Method getBuildPermMethod = theGaffer.getClass().getMethod("hasBuildPermission", Player.class, Location.class);
                return (boolean) getBuildPermMethod.invoke(null, player, location);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, "Error getting BuildPermission from TheGaffer", ex);
                return true;
            }
        }
    }
    
    public static String getGafferProtectionMessage(Player player, Location location) {
        Plugin theGaffer = Bukkit.getPluginManager().getPlugin("TheGaffer");
        if(theGaffer == null) {
            return "";
        } else {
            try {
                Method getBuildPermMethod = theGaffer.getClass().getMethod("getBuildProtectionMessage", Player.class, Location.class);
                return (String) getBuildPermMethod.invoke(null, player, location);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(PluginData.class.getName()).log(Level.SEVERE, "Error getting BuildProtectionMessage from TheGaffer", ex);
                return "";
            }
        }
    }
    
    public static String getRpUrl(String rpKey) {
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
    }
    
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
}
