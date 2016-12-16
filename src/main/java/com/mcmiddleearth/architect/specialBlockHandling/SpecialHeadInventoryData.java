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

import com.mcmiddleearth.architect.customHeadManager.CustomHeadCollection;
import com.mcmiddleearth.architect.customHeadManager.CustomHeadManagerData;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialHeadInventoryData {
    
    @Getter
    private static CustomInventory inventory;
    
    private static void createHeadInventory() {
        inventory = new CustomInventory("SpecialHeadInventory");
        CustomHeadCollection collection = CustomHeadManagerData.getCollection();
        addCollection(collection);
    }
    
    private static void addCollection(CustomHeadCollection collection) {
        for(String key : collection.getCustomHeads().keySet()) {
            ItemStack head = collection.getHead(key);
            inventory.add(head, "main");
        }
        for(String subName: collection.getSubCollections().keySet()) {
            addCollection(collection.getSubCollection(subName));
        }
    }
    
    public static void loadInventory() {
        if(inventory!=null) {
            inventory.destroy();
        }
        createHeadInventory();
    }
    
}
