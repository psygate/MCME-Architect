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
package com.mcmiddleearth.architect.additionalCommands;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.MessageUtil;
import java.util.LinkedHashMap;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class ArchitectCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof ConsoleCommandSender 
                || (sender instanceof Player 
                    && (PluginData.hasPermission((Player)sender, Permission.ARCHITECT_INFO)
                         || PluginData.hasPermission((Player)sender, Permission.ARCHITECT_RELOAD))))) {
            CommonMessages.sendNoPermissionError(sender);
            return true;
        }
        if (args.length == 0) {
            CommonMessages.sendNotEnoughArgumentsError(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("dev")) {
            if(args.length>1 && args[1].equalsIgnoreCase("true")) {
                DevUtil.setConsoleOutput(true);
                showDetails(sender);
                return true;
            }
            else if(args.length>1 && args[1].equalsIgnoreCase("false")) {
                DevUtil.setConsoleOutput(false);
                showDetails(sender);
                return true;
            }
            else if(args.length>1) {
                try {
                    int level = Integer.parseInt(args[1]);
                    DevUtil.setLevel(level);
                    showDetails(sender);
                    return true;
                }
                catch(NumberFormatException e){};
            }
            if(sender instanceof Player) {
                Player player = (Player) sender;
                if(args.length>1 && args[1].equalsIgnoreCase("-r")) {
                    DevUtil.remove(player);
                    showDetails(sender);
                    return true;
                }
                DevUtil.add(player);
                showDetails(sender);
                return true;
            }
            return true;
        }
        if (args[0].toLowerCase().startsWith("world")) {
            MessageUtil.sendInfoMessage(sender,"Worlds:");
            for(String name: PluginData.getWorldNames()) {
                if(sender instanceof Player) {
                    LinkedHashMap<String,String> message = new LinkedHashMap<>();
                    message.put(MessageUtil.getNOPREFIX()+ChatColor.AQUA+"- ",null);
                    message.put(name,"/mvtp "+name);
                    MessageUtil.sendClickableMessage((Player)sender, message);
                } else {
                    MessageUtil.sendNoPrefixInfoMessage(sender, "- "+name);
                }
            }
        } else if (args[0].equalsIgnoreCase("version")) {
            MessageUtil.sendInfoMessage(sender, "Version: "+ ArchitectPlugin.getPluginInstance()
                                                     .getDescription().getVersion());
        } else if (args[0].equalsIgnoreCase("reload")) {
            if(!(sender instanceof ConsoleCommandSender 
                    || PluginData.hasPermission((Player)sender, Permission.ARCHITECT_RELOAD))) {
                CommonMessages.sendNoPermissionError(sender);
                return true;
            }
            MessageUtil.sendInfoMessage(sender, "Reloading...");
            PluginData.load();
            MessageUtil.sendInfoMessage(sender,  "Reload complete!");
        } else {
            CommonMessages.sendInvalidSubcommandError(sender);
        }
        return true;
    }
    
    private void showDetails(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"DevUtil: Level - "+DevUtil.getLevel()+"; Console - "+DevUtil.isConsoleOutput()+"; ");
        MessageUtil.sendNoPrefixInfoMessage(cs,"Developer:");
        for(OfflinePlayer player:DevUtil.getDeveloper()) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "            "+player.getName());
        }
    }

}
