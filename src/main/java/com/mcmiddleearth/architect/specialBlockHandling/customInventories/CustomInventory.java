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
package com.mcmiddleearth.architect.specialBlockHandling.customInventories;

/**
 *
 * @author Eriol_Eandur
 */

import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialItemInventoryData;
import com.mcmiddleearth.architect.ArchitectPlugin;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
 
public class CustomInventory implements Listener {
 
    //sum should be 54
    public static final int CATEGORY_SLOTS = 9;
    public static final int ITEM_SLOTS = 45;
    
    private final String name;
   
    private final Map<String, CustomInventoryCategory> categories = new LinkedHashMap<>();
    
    private final Map<Inventory,CustomInventoryState> openInventories = new HashMap<>();
    
    //Category names -> Items in that Category
    //private final Map<String,List<ItemStack>> items = new LinkedHashMap<>();
    
    //Category names -> Items to display that Category
    //private final Map<String,ItemStack> categoryItems = new LinkedHashMap<>();
    
    //Inventory currently shown to a player -> index of Item in upper left (for scrolling)
    //private final Map<Inventory,Integer> inventoryFirstItems = new HashMap<>();
    
    //Inventory currently shown to a player -> name of currently shown category
    //private final Map<Inventory,String> inventoryCategories = new HashMap<>();

    //Inventory currently shown to a player -> name of most left category (for category scrolling)
    //private final Map<Inventory,String> inventoryFirstCategories = new HashMap<>();
    
    private final String menueItemId = "MCME Inventory Category";
   
    public CustomInventory(String name) {
        this.name = name;
        Plugin plugin = ArchitectPlugin.getPluginInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
   
    public CustomInventory add(ItemStack item, String category) {//, String name, String... info) {
        createCategoryIfNotExists(category, null, true,
                                            new ItemStack(Material.GOLD_NUGGET),
                                            new ItemStack(Material.GOLD_NUGGET));
        categories.get(category).addItem(item);
        /*List<ItemStack> itemList = items.get(category);
        itemList.add(item);//setItemNameAndLore(item, name, info));*/
        return this;
    }
   
    /*public void setCategoryItem(String category, ItemStack item) {
        createCategoryIfNotExists(category, item, item);
    }*/
    
    public void setCategoryItems(String category, UUID owner, boolean isPublic,
                                 ItemStack item, ItemStack currentItem) {
        createCategoryIfNotExists(category, owner, isPublic, item, currentItem);
        CustomInventoryCategory cat = categories.get(category);
        if(item!=null) 
            cat.setCategoryItem(setMenueItemMeta(item,category));
        if(currentItem!=null)
            cat.setCurrentCategoryItem(setMenueItemMeta(currentItem,category));
    }
    
    private void createCategoryIfNotExists(String category, UUID owner, boolean isPublic, ItemStack item, ItemStack currentItem) {
        if(!categories.containsKey(category)) {
            categories.put(category, new CustomInventoryCategory(owner, isPublic,
                                                                 setMenueItemMeta(item,category),
                                                                 setMenueItemMeta(currentItem,category)));
        }
        /*List<ItemStack> itemList = items.get(category);
        if(itemList==null) {
            itemList = new ArrayList<>();
            items.put(category, itemList);
            categoryItems.put(category, setMenueItemMeta(item, category));
        }*/
    }
    
 /*   public void showCat() {
        Logger.getGlobal().info("#############Categories###################");
        for(String cat: categories.keySet()) {
Logger.getGlobal().info(cat);
        }
    }*/
    
    public void open(Player player) {
        Set<String> categoryNames = categories.keySet();
        String startCategory = categoryNames.iterator().next();
        if(startCategory == null) {
            startCategory = "";
        }
        int size = CATEGORY_SLOTS + ITEM_SLOTS;//Math.min((items.get(startCategory).size()/9+1)*9,54);
        Inventory inventory = Bukkit.createInventory(player, size, name);
        /*for (int i = 0; i < Math.min(items.size(),54); i++) {
            inventory.setItem(i, items.get(i));
        }*/
        /*inventoryFirstItems.put(inventory, 0);
        inventoryCategories.put(inventory, startCategory);*/
        CustomInventoryState state = new CustomInventoryState(categories, inventory, player);
        openInventories.put(inventory, state);
        state.update();
        //fillInventory(inventory, startCategory, 0);
        
        player.openInventory(inventory);
    }
    
    public void deleteCategory(String name) {
        categories.remove(name);
    }
    
    public CustomInventoryCategory getCategory(String name) {
        return categories.get(name);
    }
    
    public void destroy() {
        HandlerList.unregisterAll(this);
        categories.clear();
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryDrag(final InventoryDragEvent event) {
        if (openInventories.containsKey(event.getInventory())) {//.getTitle().equals(name)) {
            for(int slot : event.getRawSlots()) {
                if(slot<event.getInventory().getSize()) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onInventoryClick(final InventoryClickEvent event) {
        if (openInventories.containsKey(event.getInventory())) { //.getTitle.equals(name)) {
            if(event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)
                    || event.getRawSlot() >= event.getInventory().getSize()
                    || event.getRawSlot() < CATEGORY_SLOTS) {//items.size()/9+1)*9 
                return;
            }
            event.setCancelled(true);
            CustomInventoryState state = openInventories.get(event.getInventory());
//Logger.getGlobal().info("click "+event.getRawSlot());
            if(state.isPageUpSlot(event.getRawSlot())) {
//Logger.getGlobal().info("click at up");
                state.pageUp();
                state.update();
                return;
            }
            if(state.isPageDownSlot(event.getRawSlot())) {
//Logger.getGlobal().info("click at down");
                state.pageDown();
                state.update();
                return;
            }
            if(event.getCursor().getType().equals(Material.AIR)) {
                event.setCursor(new ItemStack(event.getCurrentItem()));
            } else if(event.getCursor().isSimilar(event.getCurrentItem())) { 
                if(event.getCursor().getMaxStackSize()>event.getCursor().getAmount()) {
                    event.getCursor().setAmount(event.getCursor().getAmount()+1);
                }
            } else {
                event.setCursor(new ItemStack(Material.AIR));
            }
        }
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onMenueClick(final InventoryClickEvent event) {
        if (openInventories.containsKey(event.getInventory())) { //.getTitle.equals(name)) {
            if(event.getSlotType().equals(InventoryType.SlotType.OUTSIDE)
                    || event.getRawSlot() >= CATEGORY_SLOTS
                    || event.getCurrentItem()==null) {//items.size()/9+1)*9 
                return;
            }
            event.setCancelled(true);
            CustomInventoryState state = openInventories.get(event.getInventory());
            if(state.isPageLeftSlot(event.getRawSlot())) {
                state.pageLeft();
                state.update();
                return;
            }
            if(state.isPageRightSlot(event.getRawSlot())) {
                state.pageRight();
                state.update();
                return;
            }
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if(meta!=null && meta.hasLore() && meta.getLore().size()>1 && meta.getLore().get(0).equals(menueItemId)) {
                //setCategory(event.getInventory(),meta.getLore().get(1));
                state.setCategory(meta.getLore().get(1));
                state.update();
            }
        }
    }
        
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    void onScroll(final InventoryClickEvent event) {
        if(openInventories.containsKey(event.getInventory())) {
            //int first = inventoryFirstItems.get(event.getInventory());
            //String category = inventoryCategories.get(event.getInventory());t
            CustomInventoryState state = openInventories.get(event.getInventory());
            if(event.getClick().equals(ClickType.SHIFT_RIGHT)) {
                state.pageDown();
                state.update();
                /*if(first<items.get(category).size()-event.getInventory().getSize()) {
                    first+=event.getInventory().getSize();
                    inventoryFirstItems.put(event.getInventory(), first);
                    event.getInventory().clear();
                    fillInventory(event.getInventory(), category, first);
                }*/
                event.setCancelled(true);
            } else if(event.getClick().equals(ClickType.SHIFT_LEFT)) {
                /*if(first>0) {
                    first-=event.getInventory().getSize();
                    if(first<0) first = 0;
                    inventoryFirstItems.put(event.getInventory(), first);
                    event.getInventory().clear();
                    fillInventory(event.getInventory(), category, first);
                }*/
                state.pageUp();
                state.update();
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    void onCategoryChange(final InventoryClickEvent event) {
        if(openInventories.containsKey(event.getInventory())
                && event.getAction().equals(InventoryAction.NOTHING)
                && event.getSlotType().equals(SlotType.OUTSIDE)) {
            CustomInventoryState state = openInventories.get(event.getInventory());
            //String category = inventoryCategories.get(event.getInventory());
            if(event.getClick().equals(ClickType.LEFT)) {
                state.previousCategory();
                state.update();
                event.setCancelled(true);
                /*Iterator<String> iterator = items.keySet().iterator();
                if(!iterator.hasNext()) {
                    return;
                }
                String lastCat = null;
                String cat = iterator.next();
                while(iterator.hasNext() && !cat.equals(category)) {
                    lastCat = cat;
                    cat = iterator.next();
                }
                if(cat.equals(category) && lastCat != null) {
                    category = lastCat;
                } else {
                    return;
                }
                setCategory(event.getInventory(), category);*/
            } else if(event.getClick().equals(ClickType.RIGHT)){
                state.nextCategory();
                state.update();
                event.setCancelled(true);
                /*Iterator<String> iterator = items.keySet().iterator();
                String cat = "";
                while(iterator.hasNext() && !cat.equals(category)) {
                    cat = iterator.next();
                }
                if(cat !=null && cat.equals(category) && iterator.hasNext()) {
                    category = iterator.next();
                } else {
                    return;
                }
                setCategory(event.getInventory(), category);*/
            }
        }
    }
    
    /*private void setCategory(Inventory inventory, String category) {
        CustomInventoryState state = openInventories.get(inventory);
        state.setCategory(category);
        state.update();
    }*/
     /*   
        inventory.clear();
        inventoryFirstItems.put(inventory, 0);
        inventoryCategories.put(inventory, category);
        fillInventory(inventory, category, 0);
    }*/
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    void onClose(final InventoryCloseEvent event) {
        if(openInventories.containsKey(event.getInventory())) {
            openInventories.remove(event.getInventory());
        }
    }
    
    /*private void fillInventory(Inventory inventory, String category, int first) {
        int categoryCounter=0;
        for (String categoryName : items.keySet()) {
            inventory.setItem(categoryCounter, categoryItems.get(categoryName));
            categoryCounter++;
        }
        categoryCounter = menueSize();
        List<ItemStack> itemList = items.get(category);
        if(itemList==null) {
            return;
        }
        for (int i = 0; i < inventory.getSize()-categoryCounter; i++) {
            if(first+i<itemList.size()) {
                inventory.setItem(categoryCounter+i, itemList.get(first+i));
            } else {
                inventory.setItem(categoryCounter+i, null);
            }
        }
    }*/
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
            if(name!=null) {
                im.setDisplayName(name);
            }
            if(lore!=null) {
                im.setLore(Arrays.asList(lore));
            }
        item.setItemMeta(im);
        return item;
    }
    
    private ItemStack setMenueItemMeta(ItemStack item, String category) {
        return setItemNameAndLore(item,category,new String[]{menueItemId,category});
    }

    /*private int menueSize() {
        return (categories.size()%9==0?categories.size():(categories.size()/9+1)*9);
    }*/
    
    public boolean contains(String id) {
        for(CustomInventoryCategory cat: categories.values()) {
            for(ItemStack item: cat.getItems()) {
                if(SpecialItemInventoryData.getId(item).equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public ItemStack getItem(String id) {
        for(CustomInventoryCategory cat: categories.values()) {
            for(ItemStack item: cat.getItems()) {
                if(SpecialItemInventoryData.getId(item).equals(id)) {
                    return item;
                }
            }
        }
        return null;
    }
}
