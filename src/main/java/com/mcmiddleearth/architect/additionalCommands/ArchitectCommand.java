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
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import com.mcmiddleearth.util.DevUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class ArchitectCommand extends AbstractArchitectCommand{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if(!(sender instanceof Player)) {
                PluginData.getMessageUtil().sendPlayerOnlyCommandError(sender);
                return true;
            }
            if(!PluginData.hasPermission((Player)sender,Permission.ARCHITECT_HELP)) {
                PluginData.getMessageUtil().sendNoPermissionError(sender);
                return true;
            }
            if(args.length<2 || NumericUtil.isInt(args[1])) {
                int page = 1;
                if(args.length>1 && NumericUtil.isInt((args[1]))) {
                    page = NumericUtil.getInt(args[1]);
                }
                List<FancyMessage> commandList = new ArrayList<>();
                FancyMessage header = new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                                                .addSimple("Help for "
                                                            +PluginData.getMessageUtil().STRESSED+"MCME Architect"
                                                            +PluginData.getMessageUtil().INFO+" commands.");
                for(String commandKey: ArchitectPlugin.getCommandList()) {
                    AbstractArchitectCommand executor = (AbstractArchitectCommand) ArchitectPlugin
                                                           .getPluginInstance().getCommand(commandKey).getExecutor();
                    if(sender.hasPermission(executor.getHelpPermission())) {
                        String shortHelp = executor.getShortDescription();
                        String usageHelp = executor.getUsageDescription();
                        String clickCommand = executor.getHelpCommand();
                        boolean runAtOnce = true;
                        if(clickCommand==null) {
                            clickCommand = "/"+commandKey+" ";
                            runAtOnce = false;
                        } 
                        FancyMessage cmdLine = new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                            .addFancy(ChatColor.DARK_AQUA+"/"+commandKey,
                                        clickCommand, 
                                        PluginData.getMessageUtil()
                                                .hoverFormat("/"+commandKey+usageHelp,":",true))
                            .addClickable(ChatColor.WHITE+shortHelp, clickCommand);
                        if(runAtOnce) {
                            cmdLine.setRunDirect();
                        }
                        commandList.add(cmdLine);
                    }
                }
                PluginData.getMessageUtil().sendFancyListMessage((Player)sender, header, commandList, 
                                                                 "/architect help", page);
                sendManualMessage(sender);
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("weather")) {
            if(!(sender instanceof Player)) {
                PluginData.getMessageUtil().sendPlayerOnlyCommandError(sender);
                return true;
            } 
            if(!PluginData.hasPermission((Player)sender, Permission.ARCHITECT_WEATHER)) {
                PluginData.getMessageUtil().sendNoPermissionError(sender);
                return true;
            }
            if(args.length<2) {
                PluginData.getMessageUtil().sendNotEnoughArgumentsError(sender);
                return true;
            }
            if(args[1].equalsIgnoreCase("clear")) {
                PluginData.setOverrideWeather(true);
                ((Player)sender).getWorld().setStorm(false);
                ((Player)sender).getWorld().setThundering(false);
                PluginData.getMessageUtil().sendInfoMessage(sender, "Weather changing to clear.");
                return true;
            } else if(args[1].equalsIgnoreCase("rain")) {
                PluginData.setOverrideWeather(true);
                ((Player)sender).getWorld().setStorm(true);
                PluginData.getMessageUtil().sendInfoMessage(sender, "Weather changing to rain.");
                return true;
            } else if(args[1].equalsIgnoreCase("thunder")) {
                PluginData.setOverrideWeather(true);
                ((Player)sender).getWorld().setStorm(true);
                PluginData.getMessageUtil().sendInfoMessage(sender, "Weather changing to thunder.");
                return true;
            }
            PluginData.getMessageUtil().sendInvalidSubcommandError(sender);
            return true;
        }
        if(!(sender instanceof ConsoleCommandSender 
                || (sender instanceof Player 
                    && (PluginData.hasPermission((Player)sender, Permission.ARCHITECT_INFO)
                         || PluginData.hasPermission((Player)sender, Permission.ARCHITECT_RELOAD))))) {
            PluginData.getMessageUtil().sendNoPermissionError(sender);
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
            PluginData.getMessageUtil().sendInfoMessage(sender,"Worlds:");
            for(String name: PluginData.getWorldNames()) {
                if(sender instanceof Player) {
                    new FancyMessage(MessageType.INFO_INDENTED,PluginData.getMessageUtil())
                            .addClickable("- "+PluginData.getMessageUtil().STRESSED+name, "/mvtp "+name)
                            .send((Player) sender);
                } else {
                    PluginData.getMessageUtil().sendNoPrefixInfoMessage(sender, "- "+name);
                }
            }
        /*} else  if (args[0].toLowerCase().startsWith("saveworld")) {
            if(args.length<2) {
                PluginData.getMessageUtil().sendNotEnoughArgumentsError(sender);
                return true;
                }
            World world = Bukkit.getWorld(args[1]);
            if(world!=null) {
                world.save();
                PluginData.getMessageUtil().sendInfoMessage(sender, "World '"+world.getName()+"' saved.");
                return true;
            }
            PluginData.getMessageUtil().sendErrorMessage(sender,"World not found.");
            return true;*/
        } else if (args[0].equalsIgnoreCase("reloaddata")) {
            Bukkit.getServer().reloadData();
            PluginData.getMessageUtil().sendInfoMessage(sender, "Reloading mc server data...");
        } else if (args[0].equalsIgnoreCase("version")) {
            PluginData.getMessageUtil().sendInfoMessage(sender, "Version: "+ ArchitectPlugin.getPluginInstance()
                                                     .getDescription().getVersion());
        } else if (args[0].equalsIgnoreCase("reload")) {
            if(!(sender instanceof ConsoleCommandSender 
                    || PluginData.hasPermission((Player)sender, Permission.ARCHITECT_RELOAD))) {
                PluginData.getMessageUtil().sendNoPermissionError(sender);
                return true;
            }
            PluginData.getMessageUtil().sendInfoMessage(sender, "Reloading...");
            PluginData.load();
            PluginData.getMessageUtil().sendInfoMessage(sender,  "Reload complete!");
        } else {
            PluginData.getMessageUtil().sendInvalidSubcommandError(sender);
        }
        return true;
    }
    
    private void showDetails(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs,"DevUtil: Level - "+DevUtil.getLevel()+"; Console - "+DevUtil.isConsoleOutput()+"; ");
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs,"Developer:");
        for(OfflinePlayer player:DevUtil.getDeveloper()) {
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "            "+player.getName());
        }
    }
    
   private void sendManualMessage(CommandSender cs) {
        cs.sendMessage(PluginData.getMessageUtil().HIGHLIGHT_STRESSED
                +"http://www.mcmiddleearth.com/resources/mcme-architect-manual.95");
    }


    @Override
    public String getHelpPermission() {
        return Permission.ARCHITECT_INFO.getPermissionNode();
    }

    @Override
    public String getShortDescription() {
        return ": Information about MCME Architect.";
    }

    @Override
    public String getUsageDescription() {
        return " help | world | dev | version | reload [#page]: Argument 'help' shows information about Architect commands. 'world' shows a list of all server worlds. 'dev' switches on/off debug messages. 'version' displays Architect version. 'reload' reloads Architect plugin.";
    }

}
