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

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.additionalCommands.AbstractArchitectCommand;
import com.mcmiddleearth.architect.customHeadManager.CustomHeadManagerData;
import com.mcmiddleearth.pluginutil.FileUtil;
import com.mcmiddleearth.pluginutil.NumericUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 *
 * @author Eriol_Eandur
 */
public class GetCommand extends AbstractArchitectCommand {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            PluginData.getMessageUtil().sendPlayerOnlyCommandError(cs);
            return true;
        }
        Player p = (Player) cs;
        if(!PluginData.hasPermission(p,Permission.GET_COMMAND)) {
            PluginData.getMessageUtil().sendNoPermissionError(cs);
            return true;
        }
        if(!PluginData.isModuleEnabled(p.getWorld(), Modules.SPECIAL_BLOCKS)) {
            sendNotEnabledErrorMessage(cs);
            return true;
        }
        if(args.length==0 || args[0].equalsIgnoreCase("help")) {
            int page = 1;
            if(args.length>1 && NumericUtil.isInt(args[1])) {
                page = NumericUtil.getInt(args[1]);
            }
            sendHelpMessage((Player)cs,page);
            return true;
        }
        if(args[0].toLowerCase().startsWith("log")) {
            if(!PluginData.hasPermission(p, Permission.GET_LOGS)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                getLogs(p);
                PluginData.getMessageUtil().sendInfoMessage(p, "Given six sided logs!");
            }
        } else if(args[0].toLowerCase().startsWith("door")) {
            if(!PluginData.hasPermission(p, Permission.GET_DOORS)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                getDoors(p);
                PluginData.getMessageUtil().sendInfoMessage(p, "Given half doors!");
            }
        } else if(args[0].toLowerCase().startsWith("plant")) {
            if(!PluginData.hasPermission(p, Permission.GET_PLANTS)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                getPlants(p);
                PluginData.getMessageUtil().sendInfoMessage(p, "Given plants!");
            }
        } else if(args[0].toLowerCase().startsWith("mushroom")) {
            if(!PluginData.hasPermission(p, Permission.GET_PLANTS)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                if(args.length>1 && args[1].equalsIgnoreCase("red")) {
                    getHugeMushroomsSpecial(p,Material.HUGE_MUSHROOM_2);
                } else if(args.length>1 && args[1].equalsIgnoreCase("brown")) {
                    getHugeMushroomsSpecial(p,Material.HUGE_MUSHROOM_1);
                } else  {
                    getHugeMushrooms(p);
                }
                PluginData.getMessageUtil().sendInfoMessage(p, "Given mushroom blocks!");
            }
        } else if(args[0].toLowerCase().startsWith("head")) {
            if(!PluginData.hasPermission(p, Permission.GET_HEAD)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                if(args.length>1) {
                    getHeads(p, args[1]);
                } else {
                    PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                }
            }
        } else if(args[0].toLowerCase().startsWith("slab")) {
            if(!PluginData.hasPermission(p, Permission.GET_SLABS)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                getSlabs(p, (args.length>1?args[1]:""));
                PluginData.getMessageUtil().sendInfoMessage(p, "Given double steps!");
            }
        } else if(args[0].toLowerCase().startsWith("armor")) {
            if(!PluginData.hasPermission(p, Permission.GET_ARMOR)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                if(args.length>1) {
                    getArmor(p, args[1]);
                } else {
                    PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                }
            }
        } else if(args[0].toLowerCase().startsWith("misc")) {
            if(!PluginData.hasPermission(p, Permission.GET_MISC)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            } else {
                getMiscellaneous(p);
                PluginData.getMessageUtil().sendInfoMessage(p, "Given miscellaneous blocks!");
            }
        } else {
            PluginData.getMessageUtil().sendInvalidSubcommandError(cs);
        }
        return true;
    }

    private void getLogs(Player p) {
        boolean enchant = false;
        p.getInventory().addItem(addMeta(new ItemStack(Material.LOG, 64, (short) 0,(byte) 0),"Six Sided Oak Wood", enchant));
        p.getInventory().addItem(addMeta(new ItemStack(Material.LOG, 64, (short) 0,(byte) 1),"Six Sided Spruce Wood", enchant));
        p.getInventory().addItem(addMeta(new ItemStack(Material.LOG, 64, (short) 0,(byte) 2),"Six Sided Birch Wood", enchant));
        p.getInventory().addItem(addMeta(new ItemStack(Material.LOG, 64, (short) 0,(byte) 3),"Six Sided Jungle Wood", enchant));
        p.getInventory().addItem(addMeta(new ItemStack(Material.LOG_2, 64, (short) 0,(byte) 0),"Six Sided Acacia Wood", enchant));
        p.getInventory().addItem(addMeta(new ItemStack(Material.LOG_2, 64, (short) 0,(byte) 1),"Six Sided Dark Oak Wood", enchant));
    }
    
    private void getDoors(Player p) {
        p.getInventory().addItem(addMeta(new ItemStack(Material.WOOD_DOOR, 64),"Half Oak Door",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.IRON_DOOR, 64),"Half Iron Door",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.SPRUCE_DOOR_ITEM, 64),"Half Spruce Door",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.BIRCH_DOOR_ITEM, 64),"Half Birch Door",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.JUNGLE_DOOR_ITEM, 64),"Half Jungle Door",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.ACACIA_DOOR_ITEM, 64),"Half Acacia Door",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.DARK_OAK_DOOR_ITEM, 64),"Half Dark Oak Door",true));
    }

    private void getPlants(Player p) {
        p.getInventory().addItem(addMeta(new ItemStack(Material.CARROT_ITEM, 64),"Placeable Carrot",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.POTATO_ITEM, 64),"Placeable Potato",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.WHEAT, 64),"Placeable Wheat",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.MELON_SEEDS, 64),"Placeable Melon Plant",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.PUMPKIN_SEEDS, 64),"Placeable Pumpkin Plant",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.BROWN_MUSHROOM, 64),"Placeable Brown Mushroom",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.RED_MUSHROOM, 64),"Placeable RedMushroom",true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.NETHER_STALK, 64),"Placeable Onion",true));
    }
    
    private void getHugeMushrooms(Player p) {
        p.getInventory().addItem(addMeta(new ItemStack(Material.HUGE_MUSHROOM_1, 64),MushroomBlocks.INSIDE.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.HUGE_MUSHROOM_1, 64),MushroomBlocks.ALL.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.HUGE_MUSHROOM_2, 64),MushroomBlocks.ALL.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.HUGE_MUSHROOM_1, 64),MushroomBlocks.STEM_ALL.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.HUGE_MUSHROOM_1, 64),MushroomBlocks.STEM.getDisplayName(),true));
    }
    
    private void getHugeMushroomsSpecial(Player p, Material kind) {
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.NET.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.NT.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.NWT.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.SET.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.ST.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.SWT.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.WT.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.T.getDisplayName(),true));
        p.getInventory().addItem(addMeta(new ItemStack(kind, 64),MushroomBlocks.ET.getDisplayName(),true));
    }
    
    private void getHeads(Player p, String headName) {
        headName = headName.replace('\\','/');
        while(headName.contains("//")) {
            headName = headName.replace("//","/");
        }
        if(headName.endsWith("/")) {
            headName = headName.substring(0,headName.length()-1);
        }
        File file = new File(CustomHeadManagerData.getAcceptedHeadDir(),headName);
        if(!file.exists()) {
            headName = CustomHeadManagerData.getFullName(headName);
            if(headName.equals("")) {
                return;
            }
            file = new File(CustomHeadManagerData.getAcceptedHeadDir(), headName);
        }
        if(file.exists() && file.isDirectory()) {
            File[] headFiles = file.listFiles(FileUtil.getFileExtFilter(CustomHeadManagerData.getFileExtension()));
            for(File headFile:headFiles) {
                String headFilename = headFile.getName().substring(0,headFile.getName().lastIndexOf('.'));
                ItemStack head = CustomHeadManagerData.getHead(headName+'/'+headFilename);
                p.getInventory().addItem(head);
            }
            PluginData.getMessageUtil().sendInfoMessage(p, "Given all heads from: "
                                            +PluginData.getMessageUtil().STRESSED+headName);
        } else {
            ItemStack head = CustomHeadManagerData.getHead(headName);
            if(head!=null) {
                p.getInventory().addItem(head);
                PluginData.getMessageUtil().sendInfoMessage(p, "Given head: "
                                    +PluginData.getMessageUtil().STRESSED+headName);
            } else {
                PluginData.getMessageUtil().sendErrorMessage(p, "Head not found in MCME Head Collection");
            }
        }
    }
    
    private void getSlabs(Player p, String a) {
        if (NumericUtil.isInt(a)) {
            p.getInventory().addItem(addSlab(Math.max(0,NumericUtil.getInt(a))));
        }
        else {
            for(int i=0; i<10; i++){
                p.getInventory().addItem(addSlab(i));
            }
        }
    }
    
    private static ItemStack addSlab(int slabId) {
        Material material = Material.STEP;
        String displayName = "Double ";
        byte dataValue = (byte) slabId;
        switch(slabId) {
            case 0:
                displayName = displayName +"Stone";
                break;
            case 1:
                displayName = displayName +"Sandstone";
                break;
            case 2:
                displayName = displayName +"Wood";
                dataValue = 0;
                material = Material.WOOD_STEP;
                break;
            case 3:
                displayName = displayName +"Cobblestone";
                break;
            case 4:
                displayName = displayName +"Brick";
                break;
            case 5:
                displayName = displayName +"Stone Bricks";
                break;
            case 6:
                displayName = displayName +"Nether Bricks";
                break;
            case 7:
                displayName = displayName +"Quarz";
                break;
            case 8:
                displayName = displayName +"Full Stone";
                dataValue = 0;
                break;
            case 9:
                displayName = displayName +"Red Sandstone";
                dataValue = 0;
                material = Material.STONE_SLAB2;
                break;
        }
        displayName = displayName + " Slab";
        ItemStack item = new ItemStack(material, 64, (short) 0, dataValue);
        return addMeta(item,displayName,true);
        /*ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;*/
    }
    
    private void getArmor(Player p, String color) {
        ArrayList<ItemStack> armor = new ArrayList();
        armor.add(new ItemStack(Material.LEATHER_HELMET));
        armor.add(new ItemStack(Material.LEATHER_CHESTPLATE));
        armor.add(new ItemStack(Material.LEATHER_LEGGINGS));
        armor.add(new ItemStack(Material.LEATHER_BOOTS));
        try {
            if(!color.startsWith("0x")) {
                color = "0x"+color;
            }
            java.awt.Color desiredcolor = java.awt.Color.decode(color);
            for (ItemStack is : armor) {
                LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
                meta.setColor(org.bukkit.Color.fromRGB(desiredcolor.getRed(), desiredcolor.getGreen(), desiredcolor.getBlue()));
                is.setItemMeta(meta);
                p.getInventory().addItem(is);
            }
            PluginData.getMessageUtil().sendInfoMessage(p, "Given colored leather armor!");
        } catch (NumberFormatException ex) {
            PluginData.getMessageUtil().sendErrorMessage(p, "The color " + color + " is not valid.");
        }
    }
    
    private void getMiscellaneous(Player p) {
        p.getInventory().addItem(addMeta(new ItemStack(Material.PISTON_BASE,64),"Table", true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.PISTON_STICKY_BASE,64),"Wheel", true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.CACTUS, 64),"Placeable Cactus",true));
        p.getInventory().addItem(new ItemStack(Material.DRAGON_EGG));
        p.getInventory().addItem(new ItemStack(Material.HUGE_MUSHROOM_1, 64, (short) 0));
        p.getInventory().addItem(new ItemStack(Material.HUGE_MUSHROOM_2, 64, (short) 0));
        p.getInventory().addItem(addMeta(new ItemStack(Material.FURNACE, 64),"Burning Furnace", true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.REDSTONE_TORCH_ON, 64),"Burning Torch", true));
    }

    private static ItemStack addMeta(ItemStack item, String displayName, boolean enchant) {
        ItemMeta metaData = item.getItemMeta();
        if(enchant) {
            metaData.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        metaData.setDisplayName(displayName);
        item.setItemMeta(metaData);
        return item;
    }

    private void sendNotEnabledErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Placement of special Blocks is not enabled in this world.");
    }

    @Override
    public String getHelpPermission() {
        return Permission.GET_COMMAND.getPermissionNode();
    }

    @Override
    public String getShortDescription() {
        return ": Get special blocks for building.";
    }
    @Override
    public String getUsageDescription() {
        return " <block group>: Gives you a collection of special blocks. \n "
                +ChatColor.WHITE+"Click for detailed help.";
    }
    
    @Override
    public String getHelpCommand() {
        return "/get help";
    }
    
    @Override
    protected void sendHelpMessage(Player player, int page) {
        helpHeader = "Help for "+PluginData.getMessageUtil().STRESSED+"command /get ... -";
        help = new String[][]{{"/get logs","",": Get six sided logs, useful for trees."},
                                       {"/get doors","",": Get half doors."},
                                       {"/get plants","",": Get placeable plants."},
                                       {"/get head"," <head or folder name>",": Get head"," from the MCME Head Collection. If a folder name is specified you will get all heads in that folder."},
                                       {"/get slabs"," [#data value]",": Get double slabs",". If no data value argument is specified you will get all double slabs."},
                                       {"/get armor"," <color>",": Get dyed leather armor",". Color must be hex RGB code. For example 'FF0000' for red and '000000' for black."},
                                       {"/get misc","",": Get miscellaneous blocks",". Piston tables, burning furnaces,..."}};
        super.sendHelpMessage(player, page);
    }
    
}
