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
        if(!PluginData.hasPermission(p,Permission.INV_COMMAND)) {
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
        if(args[0].startsWith("b")) {
            String rpName;
            if(args.length<2) {
                rpName = PluginData.getRpName(ResourceRegionsUtil.getResourceRegionsUrl(p));
                if(rpName.equals("")) {
                    sendNotInRpRegion(p);
                    return true;
                }
            } else {
                rpName = PluginData.matchRpName(args[1]);
                if(rpName.equals("")) {
                    sendNotAValidRpKey(p);
                    return true;
                }
            }
            if(!SpecialBlockInventoryData.hasBlockInventory(rpName)) {
                sendBlockInventoryNotFound(p);
                return true;
            }
            SpecialBlockInventoryData.openInventory(p, rpName);
            return true;
        }
        if(args[0].startsWith("h")) {
            SpecialHeadInventoryData.getInventory().open(p);
            return true;
        }
        if(args[0].startsWith("i")) {
            SpecialItemInventoryData.getInventory().open(p);
            return true;
        }
        if(args[0].equalsIgnoreCase("save")) {
            if(args.length<3) {
                PluginData.getMessageUtil().sendNotEnoughArgumentsError(cs);
                return true;
            }
            SpecialSavedInventoryData.saveInventory(p, args[1], args[2], true);
            sendInventorySavedMessage(p);
            return true;
        }
        if(args[0].startsWith("s")) {
            if(args.length>1) {
                SpecialSavedInventoryData.openInventory(p, args[1]);
            } else {
                SpecialSavedInventoryData.openInventory(p, "Gondor");
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")) {
            if(!PluginData.hasPermission(p,Permission.INV_RELOAD_COMMAND)) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                return true;
            }
            SpecialBlockInventoryData.loadInventories();
            SpecialSavedInventoryData.loadInventories();
            sendInventoryLoadedMessage(p);
            return true;
        }
        PluginData.getMessageUtil().sendInvalidSubcommandError(p);

        return true;
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

    private void sendInventoryLoadedMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p, "Special Blocks inventory reloaded");
    }

    private void sendInventorySavedMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p, "Inventory saved.");
    }
}
