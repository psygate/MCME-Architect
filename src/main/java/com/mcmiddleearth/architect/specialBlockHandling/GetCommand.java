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
import com.mcmiddleearth.architect.customHeadManager.CustomHeadManagerData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.MessageUtil;
import com.mcmiddleearth.util.NumericUtil;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
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
public class GetCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(cs);
            return true;
        }
        Player p = (Player) cs;
        if(!PluginData.hasPermission(p,Permission.GET_COMMAND)) {
            CommonMessages.sendNoPermissionError(cs);
            return true;
        }
        if(!PluginData.isModuleEnabled(p.getWorld(), Modules.SPECIAL_BLOCKS)) {
            sendNotEnabledErrorMessage(cs);
            return true;
        }
        if(args.length==0) {
            CommonMessages.sendNotEnoughArgumentsError(cs);
            return true;
        }
        if(args[0].toLowerCase().startsWith("log")) {
            if(!PluginData.hasPermission(p, Permission.GET_LOGS)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                getLogs(p);
                MessageUtil.sendInfoMessage(p, "Given six sided logs!");
            }
        } else if(args[0].toLowerCase().startsWith("door")) {
            if(!PluginData.hasPermission(p, Permission.GET_DOORS)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                getDoors(p);
                MessageUtil.sendInfoMessage(p, "Given half doors!");
            }
        } else if(args[0].toLowerCase().startsWith("plant")) {
            if(!PluginData.hasPermission(p, Permission.GET_PLANTS)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                getPlants(p);
                MessageUtil.sendInfoMessage(p, "Given plants!");
            }
        } else if(args[0].toLowerCase().startsWith("food")) {
            if(!PluginData.hasPermission(p, Permission.GET_FOOD)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                getFood(p);
                MessageUtil.sendInfoMessage(p, "Given special food heads!");
            }
        } else if(args[0].toLowerCase().startsWith("head")) {
            if(!PluginData.hasPermission(p, Permission.GET_HEAD)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                if(args.length>1) {
                    ItemStack head = CustomHeadManagerData.getHead(args[1]);
                    if(head!=null) {
                        p.getInventory().addItem(head);
                        MessageUtil.sendInfoMessage(p, "Given head: "+MessageUtil.STRESSED+args[1]);
                    } else {
                        MessageUtil.sendErrorMessage(p, "Head not found in MCME Head Collection");
                    }
                } else {
                    CommonMessages.sendNotEnoughArgumentsError(cs);
                }
            }
        } else if(args[0].toLowerCase().startsWith("slab")) {
            if(!PluginData.hasPermission(p, Permission.GET_SLABS)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                getSlabs(p, (args.length>1?args[1]:""));
                MessageUtil.sendInfoMessage(p, "Given double steps!");
            }
        } else if(args[0].toLowerCase().startsWith("armor")) {
            if(!PluginData.hasPermission(p, Permission.GET_ARMOR)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                if(args.length>1) {
                    getArmor(p, args[1]);
                } else {
                    CommonMessages.sendNotEnoughArgumentsError(cs);
                }
            }
        } else if(args[0].toLowerCase().startsWith("misc")) {
            if(!PluginData.hasPermission(p, Permission.GET_MISC)) {
                CommonMessages.sendNoPermissionError(cs);
            } else {
                getMiscellaneous(p);
                MessageUtil.sendInfoMessage(p, "Given miscellaneous blocks!");
            }
        } else {
            CommonMessages.sendInvalidSubcommandError(cs);
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
    }
    
    private void getFood(Player p) {
        /*getHead(p,"MHF_Cake","Cake");
        getHead(p,"_Grime","Bread");
        getHead(p,"MHF_Cactus","Melon");
        getHead(p,"MHF_Pumpkin","Pumpkin");
        getHead(p,"MHF_Melon","Salad");
        getHead(p,"MHF_Apple","Apple");
        getHead(p,"Ernie77","Roast");
        getHead(p,"JoeTheManMC","Cheese");*/
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Cheese\"},SkullOwner:{Id:\"9c919b83-f3fe-456f-a824-7d1d08cc8bd2\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU1ZDYxMWE4NzhlODIxMjMxNzQ5YjI5NjU3MDhjYWQ5NDI2NTA2NzJkYjA5ZTI2ODQ3YTg4ZTJmYWMyOTQ2In19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Cheese2\"},SkullOwner:{Id:\"fedf6ee0-8573-4588-89cf-5951e2596795\",Properties:{textures:[{Value:\"aHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lZjgzNjJiMDdkNzdhNjAyNzJiODEyNTQ0ODI2ODM0ODJhYjk1NDZlZmFjNjk1MDM5NWViNmY3NGIxMQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Purple Grapes\"},SkullOwner:{Id:\"7815481b-f563-4ece-af98-64e941b82239\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU1OTM1ODYzYzUzYTk5NmY1MzM0ZTkwZjU1ZGU1MzhlODNmZmM1ZjZiMGI4ZTgzYTRkYzRmNmU2YjEyMDgifX19\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Chest\"},SkullOwner:{Id:\"148ce164-81e8-43d3-b057-4b21cf96d9d3\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmY2OGQ1MDliNWQxNjY5Yjk3MWRkMWQ0ZGYyZTQ3ZTE5YmNiMWIzM2JmMWE3ZmYxZGRhMjliZmM2ZjllYmYifX19\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Iron Chest\"},SkullOwner:{Id:\"90ff743f-323c-49e0-a239-aaa2117b4fc0\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThlNTU0NGFmN2Y1NDg5Y2MyNzQ5MWNhNjhmYTkyMzg0YjhlYTVjZjIwYjVjODE5OGFkYjdiZmQxMmJjMmJjMiJ9fX0=\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Medieval Beer\"},SkullOwner:{Id:\"0be2d451-8ae5-4250-a0f1-74f44846ded2\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjI5NjAzZDgyOTYzMDU2YmUxMzUyMmNmYjdkNDUyMGM3NmJhNjg3ZjM5NmEwZGFiMTI1ZTYzYjVkYWNlYTgifX19\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Mushroom Stew\"},SkullOwner:{Id:\"e38a4b23-e498-4c8e-8415-ca0b313d05bb\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MxNDE0NGY2MWM0ZTY2YjNjNDQzNjYwZGViYzczY2IyMTI1ZDAxNDBjNTFiNTUyMmM4YTY4Yjc4OTQxNCJ9fX0=\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Ham Cheese Sandwich\"},SkullOwner:{Id:\"4544cee0-6c8a-419b-9844-86665e05015b\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZTg0ZDE5Yzg1YWZmNzk2Yzg4YWJkYTIxZWM0YzkyYzY1NWUyZDY3YjcyZTVlNzdiNWFhNWU5OWVkIn19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Potted Rose Plant\"},SkullOwner:{Id:\"2d8ef167-5297-4aa4-adf5-5d1d36bce776\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWRiYTM4ZTlmYzY3ZjcyYzQ1OGZkYWM4ZWNkN2NhYmFlZDNlYjgzNzM3MTQzYTAxMjgzNTBhMWFiMzgxZTNlIn19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Potted Salvia Plant\"},SkullOwner:{Id:\"4e49245d-0ac2-4c6e-aff8-2894c8878eec\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ4MGMyNmY5MDRiNTdlNjMxZTM5ZWJjNDQ2ZWMxYWYyZGNlMzQzMmViODQzMWZiZDE5MDg3YWRiNGFiY2IifX19\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Pile of Books\"},SkullOwner:{Id:\"6e166933-a807-4a73-8a19-e44053676f56\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFjNjNkOWI5ZmQ4NzQyZWFlYjA0YzY5MjE3MmNiOWRhNDM3ODE2OThhNTc1Y2RhYmUxYzA0ZGYxMmMzZiJ9fX0=\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Plant\"},SkullOwner:{Id:\"e303e867-1764-4350-bcbf-da4ecd6616f9\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY5YzcxYWYzZjdlODE5ZWMzYzQ0OTZmMjkxNTY3YWVmYjk4ZjU1ODQ1MTdkYTI2NmJhMGE2ZWNjYWE5YTZlMyJ9fX0=\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Present\"},SkullOwner:{Id:\"7744f4b0-9aa1-4984-80f1-a5068e1a7fe2\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ3YTlmNmVkMDhkZDIxN2ZkZjA5ZjQ2NTJiZjZiN2FmNjIxZTFkNWY4OTYzNjA1MzQ5ZGE3Mzk5OGE0NDMifX19\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Stack of Books\"},SkullOwner:{Id:\"e056ebd9-3277-47d4-9a7c-87dfabd13ed3\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODJhZTE5MTA3MDg2ZGQzMTRkYWYzMWQ4NjYxOGU1MTk0OGE2ZTNlMjBkOTZkY2ExN2QyMWIyNWQ0MmQyYjI0In19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Wooden Crate\"},SkullOwner:{Id:\"9c1ec873-df5c-4aa2-9dac-291d7de98450\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWYyMmI2YTNhMGYyNGJkZWVhYjJhNmFjZDliMWY1MmJiOTU5NGQ1ZjZiMWUyYzA1ZGRkYjIxOTQxMGM4In19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Cabinet\"},SkullOwner:{Id:\"babc218e-63dc-4ba7-b452-3c271eead119\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzZWNiNTU4ZjIxYzkxODk3Nzk4N2U5NzRmMmU2YmQxMjhlYjlkOWMwNTQxNGU5NDZkZDc4NmViZGI0In19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Potted Daffodil Plant\"},SkullOwner:{Id:\"e0234bf4-a604-4c14-a2cb-342cb8490d9e\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNmZmZjODk1NWIwZTgzMDI4OThmN2YwMTVkODQ5ZjBhMDFkYmJiMDQyNzQxNzUwNmZiODllYWQ1NGQ0NWY2In19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Potted Camellia Plant\"},SkullOwner:{Id:\"e5fdfdc7-f8a4-4a77-8d7c-17d98cf8a268\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWRjNGMxMmJmMjYxOWNiZmM4ZjIyZGM2MmMwMjJjZTE1MTI2Y2VhM2UyMTJjMjhkOWY5NmVhMzEwYWM0YzQyIn19fQ==\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Old Books\"},SkullOwner:{Id:\"12c8e58d-6b45-49b6-9b63-77b57b290243\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM0NGQ4M2U3YzZlY2U5MGQzNGM2ODVhOWEzMzg0ZjhlOGQwMTUxNjMzZmMyZWVhZTRkNGI2MzY4NjJkMzMifX19\"}]}}}");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give "+p.getName()+" skull 64 3 {display:{Name:\"Potted Azalea Plant\"},SkullOwner:{Id:\"faec9b66-94ca-4b6f-b6cd-f9a54b802ae3\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM1MjU3Yjc5OWQzOTQ2OTI3ZjJiMzI1ZDM2NmViNTEwNGE1YzM1MjE5ZWU0ZTRkMzU3MjFiZjI4YTIxMCJ9fX0=\"}]}}}");
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
            java.awt.Color desiredcolor = java.awt.Color.decode(color);
            for (ItemStack is : armor) {
                LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
                meta.setColor(org.bukkit.Color.fromRGB(desiredcolor.getRed(), desiredcolor.getGreen(), desiredcolor.getBlue()));
                is.setItemMeta(meta);
                p.getInventory().addItem(is);
            }
            MessageUtil.sendInfoMessage(p, "Given colored leather armor!");
        } catch (NumberFormatException ex) {
            MessageUtil.sendErrorMessage(p, "The color " + color + " is not valid.");
        }
    }
    
    private void getMiscellaneous(Player p) {
        p.getInventory().addItem(addMeta(new ItemStack(Material.PISTON_BASE,64),"Table", true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.PISTON_STICKY_BASE,64),"Wheel", true));
        p.getInventory().addItem(addMeta(new ItemStack(Material.CACTUS, 64),"Placeable Cactus",true));
        p.getInventory().addItem(new ItemStack(Material.DRAGON_EGG));
        p.getInventory().addItem(new ItemStack(Material.HUGE_MUSHROOM_1, 64, (short) 0));
        //p.getInventory().addItem(new ItemStack(Material.BURNING_FURNACE, 64));
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
        MessageUtil.sendErrorMessage(cs, "Placement of special Blocks is not enabled in this world.");
    }



}
