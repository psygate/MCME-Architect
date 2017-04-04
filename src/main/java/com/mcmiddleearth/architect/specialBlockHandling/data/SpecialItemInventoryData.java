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

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.specialBlockHandling.customInventories.CustomInventory;
import com.mcmiddleearth.architect.specialBlockHandling.customInventories.SearchInventory;
import com.mcmiddleearth.pluginutil.FileUtil;
import com.mcmiddleearth.util.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
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
public class SpecialItemInventoryData {
    
    public final static String SPECIAL_ITEM_TAG = "MCME Item";

    private final static Map<String,CustomInventory> inventories = new HashMap<>();

    private final static Map<String, SearchInventory> searchInventories = new HashMap<>();

    //private static List<ItemStack> itemList = new ArrayList<>();
    
    private static final String configLocator = "item_inventories";
    
    private static final File configFolder = new File(ArchitectPlugin.getPluginInstance()
                                                       .getDataFolder(),"inventories/item");
    
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
        //itemList = new ArrayList<>();
        File[] files = configFolder.listFiles(FileUtil.getDirFilter());
        for(File file: files) {
            createItemInventory(file);
        }
    }
    
    private static void createItemInventory(File folder) {
        String rpName = folder.getName();
        File[] files = folder.listFiles(FileUtil.getFileExtFilter("yml"));
        CustomInventory inventory = new CustomInventory(ChatColor.WHITE+"MCME Items - "+rpName);
        inventories.put(rpName, inventory);
        SearchInventory searchInventory = new SearchInventory(ChatColor.WHITE+"items");
        searchInventories.put(rpName, searchInventory);
        for(File file: files) {
            loadFromFile(inventory, rpName, file);
        }
    }
    
    private static void loadFromFile(CustomInventory inventory, String rpName, File file) {
        Logger.getGlobal().info("Loading items into to inventory for resource pack "+rpName+" from "+file.getName());
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
            if(inventory.contains(fullName(rpName, itemKey))) {
                Logger.getLogger(SpecialBlockInventoryData.class.getName())
                    .log(Level.WARNING, "Double custom item ID "+fullName(rpName,itemKey)+"'. Item skipped.");
            } else {
                ConfigurationSection section = itemConfig.getConfigurationSection(itemKey);
                ItemStack inventoryItem = loadItemFromConfig(section, itemKey, rpName);
                if(inventoryItem!=null) {
                    String category = section.getString("category","item");
                    //itemList.add(inventoryItem);
                    inventory.add(inventoryItem, category);
                    searchInventory.add(inventoryItem);
                } else {
                    Logger.getLogger(SpecialBlockInventoryData.class.getName())
                        .log(Level.WARNING, "Invalid config data while loading Special MCME Item '"+itemKey+"'. Item skipped.");
                }
            }
        }
    }
    
    public static void openInventory(Player p, String resourcePack) {
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
    
    
    public static boolean hasItemInventory(String rpName) {
        return inventories.containsKey(rpName);
    }
    
    /*public static ItemStack getSpecialItem(String id) {
        for(ItemStack data: itemList) {
            if(getId(data).equals(id)) {
                return data;
            }
        }
        return null;
    }*/
    
    public static synchronized void downloadConfig(String rpName, InputStream in) throws IOException {
        ZipUtil.extract(PluginData.getRpUrl(rpName), in, configLocator, new File(configFolder,rpName));
    }
    
    private static ItemStack loadItemFromConfig(ConfigurationSection config, String name, String rp) {
        Material itemMat = Material.matchMaterial(config.getString("itemMaterial",""));
        short dam = (short) config.getInt("damage");
        String displayName = (String) config.get("display");
        if(displayName==null) {
            displayName = name;
        }
        if(itemMat!=null) {
            ItemStack item = new ItemStack(itemMat,1,dam);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(displayName);
            im.setLore(Arrays.asList(new String[]{SPECIAL_ITEM_TAG, fullName(rp,name)}));
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
    
    public static String getId(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta!=null && meta.hasLore()) {
            return meta.getLore().get(1);
        }
        return "";
    }
}
/*    
    @Getter
    private static CustomInventory inventory;

    private static final File configFile = new File(ArchitectPlugin.getPluginInstance()
                                                       .getDataFolder(),"specialItems.yml");
   
    private static void createItemInventory() {
        Configuration config = YamlConfiguration.loadConfiguration(configFile);
        inventory = new CustomInventory("SpecialItemInventory");
        loadItems("", config);
    }
    
    private static void loadItems(String path, ConfigurationSection config) {
        ConfigurationSection itemConfig = config.getConfigurationSection("items");
        if(itemConfig!=null) {
            for(String name: itemConfig.getKeys(false)) {
                inventory.add(deserializeItem(path, name, itemConfig.getConfigurationSection(name)),"main");
            }
        }
        ConfigurationSection subConfig = config.getConfigurationSection("folders");
        if(subConfig!=null) {
            for(String dirName: subConfig.getKeys(false)) {
                loadItems(path+"/"+dirName,subConfig.getConfigurationSection(dirName));
            }
        }
    }
    
    private static ItemStack deserializeItem(String path, String name, ConfigurationSection config) {
        ItemStack item = new ItemStack(Material.getMaterial(config.getString("material")),1, 
                             (short) config.getInt("durability"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        if(config.contains("lore")) {
            lore.addAll(config.getStringList("lore"));
        }
        lore.add("");
        lore.add(path+"/"+name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    public static void loadInventory() {
        if(inventory!=null) {
            inventory.destroy();
        }
        createItemInventory();
    }
    
    
}
*/