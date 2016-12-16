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
package com.mcmiddleearth.architect.specialBlockHandling;

import com.mcmiddleearth.architect.ArchitectPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialItemInventoryData {
    
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
