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

import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialBlockInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialItemInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialHeadInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialSavedInventoryData;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.additionalCommands.AbstractArchitectCommand;
import com.mcmiddleearth.architect.specialBlockHandling.customInventories.CustomInventoryCategory;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.util.ResourceRegionsUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class InvCommand extends AbstractArchitectCommand {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            PluginData.getMessageUtil().sendPlayerOnlyCommandError(cs);
            return true;
        }
        Player p = (Player) cs;
        if(!PluginData.isModuleEnabled(p.getWorld(), Modules.SPECIAL_BLOCKS)) {
            sendNotEnabledErrorMessage(cs);
            return true;
        }
        if(!PluginData.hasPermission(p,Permission.INV_COMMAND)) {
            PluginData.getMessageUtil().sendNoPermissionError(cs);
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
        if(args[0].equalsIgnoreCase("reload")) {
            if(!PluginData.hasPermission(p,Permission.INV_RELOAD_COMMAND)) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                return true;
            }
            SpecialBlockInventoryData.loadInventories();
            SpecialItemInventoryData.loadInventories();
            SpecialHeadInventoryData.loadInventory();
            SpecialSavedInventoryData.loadInventories();
            sendInventoryLoadedMessage(p);
            return true;
        }
        if(args[0].startsWith("h")) {
            if(args.length>1 && args[1].equalsIgnoreCase("search")) {
                if(args.length<3) {
                    PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                    return true;
                }
                SpecialHeadInventoryData.openSearchInventory(p,args[2]);
                return true;
            }
            SpecialHeadInventoryData.openInventory(p);
            return true;
        }
        int rpIndex = 0;
        String rpName = "";
        for(int i=1; i<args.length;i++) {
            if(args[i].startsWith("rp:")) {
                rpName = PluginData.matchRpName(args[i].substring(3));
                if(rpName.equals("")) {
                    sendNotAValidRpKey(p);
                    return true;
                }
                rpIndex = i;
                break;
            }
        }
        if(rpIndex == 0) {
            rpName = PluginData.getRpName(ResourceRegionsUtil.getResourceRegionsUrl(p));
            if(rpName.equals("")) {
                sendNotInRpRegion(p);
                return true;
            }
        }
        if(args[0].equalsIgnoreCase("create")) {
            if(!PluginData.hasPermission(p,Permission.INV_SAVE)) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                return true;
            }
            if(args.length<=adaptIndex(1,rpIndex)) {
                PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                return true;
            }
            if(SpecialSavedInventoryData.categoryExists(args[adaptIndex(1,rpIndex)], rpName)) {
                sendInventoryAlreadyExistsError(p);
                return true;
            }
            SpecialSavedInventoryData.saveInventory(p, args[adaptIndex(1,rpIndex)], rpName, true);
            sendInventorySavedMessage(p);
            return true;
        }
        if(args[0].equalsIgnoreCase("delete")) {
            if(!PluginData.hasPermission(p,Permission.INV_SAVE)) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                return true;
            }
            if(args.length<=adaptIndex(1,rpIndex)) {
                PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                return true;
            }
            if(!SpecialSavedInventoryData.categoryExists(args[adaptIndex(1,rpIndex)], rpName)) {
                sendInventoryNotFoundError(p);
                return true;
            }
            CustomInventoryCategory category = SpecialSavedInventoryData
                                                 .getCategory(args[adaptIndex(1,rpIndex)], rpName);
            if(!category.getOwner().equals(p.getUniqueId()) 
                        && !PluginData.hasPermission(p, Permission.INV_OTHER)) {
                PluginData.getMessageUtil().sendNoPermissionError(cs);
            }
            SpecialSavedInventoryData.deleteInventory(args[adaptIndex(1,rpIndex)], rpName);
            sendInventoryDeletedMessage(p);
            return true;
        }
        if(args[0].startsWith("s") || args[0].startsWith("c")) {
            SpecialSavedInventoryData.openInventory(p, rpName);
            return true;
        }
        boolean search = false;
        if(args.length>adaptIndex(1,rpIndex) && (args[0].startsWith("b") || args[0].startsWith("i"))) {
            if(args[adaptIndex(1,rpIndex)].equalsIgnoreCase("search")) {
                search=true;
                if(args.length<=adaptIndex(2,rpIndex)) {
                    PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                    return true;
                }
            }
        }
        /*if(searchIndex==0) {
            if(args.length>2 && args[2].equalsIgnoreCase("search")) {
                searchIndex = 3;
                if(args.length<4) {
                    PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                    return true;
                }
            }
        }*/
        if(args[0].startsWith("b")) {
            if(!SpecialBlockInventoryData.hasBlockInventory(rpName)) {
                sendBlockInventoryNotFound(p);
                return true;
            }
            if(search) {
                SpecialBlockInventoryData.openSearchInventory(p, rpName, args[adaptIndex(2,rpIndex)]);
            } else {
                SpecialBlockInventoryData.openInventory(p, rpName);
            }
            return true;
        } 
        if(args[0].startsWith("i")) {
            if(!SpecialItemInventoryData.hasItemInventory(rpName)) {
                sendItemInventoryNotFound(p);
                return true;
            }
            if(search) {
                SpecialItemInventoryData.openSearchInventory(p, rpName, args[adaptIndex(2,rpIndex)]);
            } else {
                SpecialItemInventoryData.openInventory(p, rpName);
            }
            return true;
        }
        PluginData.getMessageUtil().sendInvalidSubcommandError(p);
        return true;
        /*if(args[1].equals("search")) {
            String rpName="";
            String search = "";
            if(args.length<3) {
                rpName = PluginData.getRpName(ResourceRegionsUtil.getResourceRegionsUrl(p));
                if(rpName.equals("")) {
                    sendNotInRpRegion(p);
                    return true;
                }
                search = args[1];
            } else {
                rpName = PluginData.matchRpName(args[1]);
                if(rpName.equals("")) {
                    sendNotAValidRpKey(p);
                    return true;
                }
                search = args[2];
            }
            if(args.length>3 && args[3].startsWith("-i")) {
                if(!SpecialItemInventoryData.hasItemInventory(rpName)) {
                    sendItemInventoryNotFound(p);
                    return true;
                }
                SpecialItemInventoryData.openSearchInventory(p, rpName, search);
                return true;
            } else {
                if(!SpecialBlockInventoryData.hasBlockInventory(rpName)) {
                    sendBlockInventoryNotFound(p);
                    return true;
                }
                SpecialBlockInventoryData.openSearchInventory(p, rpName, search);
                return true;
            }
        }*/
    }
    
    private int adaptIndex(int index, int rpIndex) {
        if(rpIndex==0) {
            return index;
        } else if(rpIndex<=index) {
            return index+1;
        } else {
            return index;
        }
    }
    
    private void sendNotEnabledErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Custom build inventories are not enabled in this world.");
    }
    
    private void sendNotInRpRegion(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You are not in a valid resource pack region. Please use '/inv b <resource pack>'.");
    }
    
    private void sendBlockInventoryNotFound(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "No block inventory found for this resource pack.");
    }
    
    private void sendNotAValidRpKey(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Resource pack not found.");
    }
    
    private void sendInventoryLoadedMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p, "Special Blocks inventory reloaded");
    }

    private void sendInventorySavedMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p, "Inventory saved.");
    }

    private void sendInventoryDeletedMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p, "Inventory deleted.");
    }

    private void sendItemInventoryNotFound(Player p) {
        PluginData.getMessageUtil().sendErrorMessage(p, "No item inventory found for this resource pack.");
    }
    
    private void sendInventoryAlreadyExistsError(Player p) {
        PluginData.getMessageUtil().sendErrorMessage(p,"An inventory with that name already exists."); 
    }

    private void sendInventoryNotFoundError(Player p) {
        PluginData.getMessageUtil().sendErrorMessage(p,"No inventory with that name."); 
    }

    @Override
    public String getHelpPermission() {
        return Permission.ITEM_TEX_COMMAND.getPermissionNode();
    }

    @Override
    public String getShortDescription() {
        return ": Handles custom buid inventories.";
    }
    @Override
    public String getUsageDescription() {
        return " b|reload [rp]: TBD. \n "
                +ChatColor.WHITE+"Click for detailed help.";
    }
    
    @Override
    public String getHelpCommand() {
        return "/inv help";
    }

}
