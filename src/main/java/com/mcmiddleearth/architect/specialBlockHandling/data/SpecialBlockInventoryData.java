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
package com.mcmiddleearth.architect.specialBlockHandling.data;

import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockSixFaces;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockThinWall;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockItemBlock;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockThreeAxis;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockTwoAxis;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockWallCombi;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockFourDirections;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockEightFaces;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockDoor;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockDoorFourBlocks;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockDoorThreeBlocks;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockFiveFaces;
import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.specialBlockHandling.customInventories.CustomInventory;
import com.mcmiddleearth.architect.specialBlockHandling.customInventories.SearchInventory;
import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockType;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockBurningFurnace;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockItemFourDirections;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockItemTwoDirections;
import com.mcmiddleearth.pluginutil.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockInventoryData {
    
    public final static String SPECIAL_BLOCK_TAG = "MCME Block";

    private final static Map<String,CustomInventory> inventories = new HashMap<>();
    
    private final static Map<String, SearchInventory> searchInventories = new HashMap<>();

    private static List<SpecialBlock> blockList = new ArrayList<>();
    
    private static final File configFolder = new File(ArchitectPlugin.getPluginInstance()
                                                       .getDataFolder(),"inventories/block");
    
    static {
        if(!configFolder.exists()) {
            configFolder.mkdir();
        }
    }
    public static void loadInventories() {
        if(!inventories.isEmpty()) {
            for(CustomInventory inv:inventories.values()) {
                inv.destroy();
            }
            inventories.clear();
        }
        blockList = new ArrayList<>();
        File[] files = configFolder.listFiles(FileUtil.getDirFilter());
        for(File file: files) {
            createBlockInventory(file);
        }
    }
    
    private static void createBlockInventory(File folder) {
        String rpName = folder.getName();
        File[] files = folder.listFiles(FileUtil.getFileExtFilter("yml"));
        CustomInventory inventory = new CustomInventory(ChatColor.WHITE+"MCME Blocks - "+rpName);
        inventories.put(rpName, inventory);
        SearchInventory searchInventory = new SearchInventory(ChatColor.WHITE+"blocks");
        searchInventories.put(rpName, searchInventory);
        File blockFile = new File(folder,"block.yml");
        if(blockFile.exists()) {
            loadFromFile(rpName, blockFile);
        }
        for(File file: files) {
            if(!file.getName().equals(blockFile.getName())) {
                loadFromFile(rpName, file);
            }
        }
    }
    
    private static void loadFromFile(String rpName, File file) {
        Logger.getGlobal().info("Loading items into to inventory for resource pack "+rpName+" from "+file.getName());
        CustomInventory inventory = inventories.get(rpName);
        SearchInventory searchInventory = searchInventories.get(rpName);
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(SpecialBlockInventoryData.class.getName()).log(Level.SEVERE, null, ex);
        }
        ConfigurationSection categoryConfig = config.getConfigurationSection("Categories");
        if(categoryConfig!=null) {
            for(String categoryKey: categoryConfig.getKeys(false)) {
                ConfigurationSection section = categoryConfig.getConfigurationSection(categoryKey);
                ItemStack categoryItem = loadItemFromConfig(section, categoryKey, rpName);
                ItemStack currentCategoryItem = loadItemFromConfig(section, categoryKey, rpName);
                if(section.contains("damageCurrent")) {
                    currentCategoryItem.setDurability((short)section.getInt("damageCurrent"));
                }
                inventory.setCategoryItems(categoryKey, null, true, categoryItem, currentCategoryItem);
            }
        }
        ConfigurationSection itemConfig = config.getConfigurationSection("Items");
        if(itemConfig==null) {
            itemConfig = config;
        }
        for(String itemKey: itemConfig.getKeys(false)) {
            if(getSpecialBlock(fullName(rpName, itemKey))!=null) {
                Logger.getLogger(SpecialBlockInventoryData.class.getName())
                    .log(Level.WARNING, "Double custom block ID "+fullName(rpName,itemKey)+"'. Block skipped.");
            } else {
                SpecialBlockType type;
                ConfigurationSection section = itemConfig.getConfigurationSection(itemKey);
                try {
                    type = SpecialBlockType.valueOf(section.getString("type"));
                } catch( IllegalArgumentException e) { 
                    type = SpecialBlockType.INVALID;
                }
                SpecialBlock blockData = null;
                switch(type) {
                    case BLOCK:
                        blockData = SpecialBlock.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case THREE_AXIS:
                        blockData = SpecialBlockThreeAxis.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case TWO_AXIS:
                        blockData = SpecialBlockTwoAxis.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case FIVE_FACES:
                        blockData = SpecialBlockFiveFaces.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case SIX_FACES:
                        blockData = SpecialBlockSixFaces.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case EIGHT_FACES:
                        blockData = SpecialBlockEightFaces.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case FOUR_DIRECTIONS:
                        blockData = SpecialBlockFourDirections.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case WALL_COMBI:
                        blockData = SpecialBlockWallCombi.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case DOOR:
                        blockData = SpecialBlockDoor.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case THIN_WALL:
                        blockData = SpecialBlockThinWall.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case DOOR_FOUR_BLOCKS:
                        blockData = SpecialBlockDoorFourBlocks.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case DOOR_THREE_BLOCKS:
                        blockData = SpecialBlockDoorThreeBlocks.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case ITEM_BLOCK:
                        blockData = SpecialBlockItemBlock.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case ITEM_BLOCK_TWO_DIRECTIONS:
                        blockData = SpecialBlockItemTwoDirections.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case ITEM_BLOCK_FOUR_DIRECTIONS:
                        blockData = SpecialBlockItemFourDirections.loadFromConfig(section, fullName(rpName,itemKey));
                        break;
                    case BURNING_FURNACE:
                        blockData = SpecialBlockBurningFurnace.loadFromConfig(section, fullName(rpName, itemKey));
                }
                ItemStack inventoryItem = loadItemFromConfig(section, itemKey, rpName);
                if(blockData !=null && inventoryItem!=null) {
                    String category = section.getString("category","Block");
                    blockList.add(blockData);
                    inventory.add(inventoryItem, category);
                    searchInventory.add(inventoryItem);
                } else {
                    Logger.getLogger(SpecialBlockInventoryData.class.getName())
                        .log(Level.WARNING, "Invalid config data while loading Special MCME Block '"+itemKey+"'. Block skipped.");
                    Logger.getGlobal().info("block Data: "+blockData+" - item: "+inventoryItem);
                }
            }
        }
    }
    
    public static void openInventory(Player p, String resourcePack) {
        /*else {
            resourcePack = resourcePack.toLowerCase();
        }
        if(resourcePack.toLowerCase().startsWith("e") 
                || ArchitectPlugin.getPluginInstance().getConfig().get("Eriador").equals(resourcePack)) {
            resourcePack = RP_ERIADOR;
        } else if(resourcePack.toLowerCase().startsWith("r") 
                || ArchitectPlugin.getPluginInstance().getConfig().get("Rohan").equals(resourcePack)) {
            resourcePack = RP_ROHAN;
        } else if(resourcePack.toLowerCase().startsWith("d") 
                || ArchitectPlugin.getPluginInstance().getConfig().get("Dwarf").equals(resourcePack)) {
            resourcePack = RP_DWARF;
        } else if(resourcePack.toLowerCase().startsWith("m") 
                || ArchitectPlugin.getPluginInstance().getConfig().get("Mordor").equals(resourcePack)) {
            resourcePack = RP_MORDOR;
        } else if(resourcePack.toLowerCase().startsWith("l") 
                || ArchitectPlugin.getPluginInstance().getConfig().get("Lothlorien").equals(resourcePack)) {
            resourcePack = RP_LOTHLORIEN;
        } else {
            resourcePack = RP_GONDOR;
        }*/
        CustomInventory inv = inventories.get(resourcePack);
        if(inv==null) {
            inv = inventories.get("Gondor");
        }
        if(inv!=null) {
            inv.open(p);
        }
    }
    
    public static void openSearchInventory(Player p, String resourcePack, String search) {
        SearchInventory inv = searchInventories.get(resourcePack);
        if(inv==null) {
            inv = searchInventories.get("Gondor");
        }
        if(inv!=null) {
            inv.open(p, search);
//Logger.getGlobal().info("Inventory 3");
        }
    }
    
    public static boolean hasBlockInventory(String rpName) {
        return inventories.containsKey(rpName);
    }
    
    public static SpecialBlock getSpecialBlock(String id) {
        for(SpecialBlock data: blockList) {
            if(data.getId().equals(id)) {
                return data;
            }
        }
        return null;
    }
    
    private static ItemStack loadItemFromConfig(ConfigurationSection config, String name, String rp) {
        Material itemMat = Material.matchMaterial(config.getString("itemMaterial",""));
        short dam = (short) config.getInt("damage",0);
        String displayName = (String) config.get("display");
        if(displayName==null) {
            displayName = name;
        }
        if(itemMat!=null) {
            ItemStack item = new ItemStack(itemMat,1,dam);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(displayName);
            im.setLore(Arrays.asList(new String[]{SPECIAL_BLOCK_TAG, fullName(rp,name)}));
            im.spigot().setUnbreakable(true);
            im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(im);
            return item;
        }
        return null;
    }
    
    private static String fullName(String rpName, String name) {
        return rpName+"/"+name;
    }
}
