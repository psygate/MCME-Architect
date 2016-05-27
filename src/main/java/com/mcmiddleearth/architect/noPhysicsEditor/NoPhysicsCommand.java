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
package com.mcmiddleearth.architect.noPhysicsEditor;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.additionalCommands.AbstractArchitectCommand;
import com.mcmiddleearth.pluginutil.NumericUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class NoPhysicsCommand extends AbstractArchitectCommand {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String c, String[] args) {
        if (!(cs instanceof Player)) {
            PluginData.getMessageUtil().sendPlayerOnlyCommandError(cs);
            return true;
        }
        if(!PluginData.hasPermission((Player)cs,Permission.NO_PHYSICS_LIST)) {
            PluginData.getMessageUtil().sendNoPermissionError(cs);
            return true;
        }
        if(!PluginData.isModuleEnabled(((Player)cs).getWorld(), Modules.NO_PHYSICS_LIST_ENABLED)) {
            sendNotEnabledErrorMessage(cs);
            return true;
        } else {
            Player p = (Player) cs;
            if(args.length<2 || args[0].equalsIgnoreCase("help")) {
                int page = 1;
                if(args.length>1 && NumericUtil.isInt(args[1])) {
                    page = NumericUtil.getInt(args[1]);
                }
                sendHelpMessage(p,page);
            } else if(args[0].equalsIgnoreCase("list")) {
                PluginData.getMessageUtil().sendInfoMessage(p, "No physics list for");
                for (String worldName : PluginData.getWorldNames()) {
                    if (worldName.equalsIgnoreCase(args[1]) || args[1].equalsIgnoreCase("-all")) {
                        PluginData.getMessageUtil().sendNoPrefixInfoMessage(p, "- "+worldName+": "
                                        +PluginData.getMessageUtil().STRESSED+ PluginData.getNpList(worldName));
                    }
                }
            } else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                if(args.length<3) {
                    PluginData.getMessageUtil().sendNotEnoughArgumentsError(p);
                    sendHelpMessage(p,1);
                    return true;
                }
                int blockId;
                if(NumericUtil.isInt(args[2])) {
                    blockId = NumericUtil.getInt(args[2]); 
                } else {
                    Material material = Material.matchMaterial(args[2]);
                    if(material==null) {
                        sendNoValidMaterial(p);
                        return true;
                    }
                    blockId = material.getId();
                }
                boolean materialAdded = false;
                boolean materialRemoved = false;
                if(args[1].equalsIgnoreCase("-all")) {
                    for(String worldName: PluginData.getWorldNames()) {
                        if(args[0].equalsIgnoreCase("add")) {
                            materialAdded = materialAdded | PluginData.addNpBlock(worldName,blockId);
                        } else {
                            materialRemoved = materialRemoved | PluginData.removeNpBlock(worldName,blockId);
                        }
                    }
                }
                else {
                    World world = Bukkit.getWorld(args[1]);
                    if(world == null) {
                        sendWorldNotFound(p);
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("add")) {
                        materialAdded = materialAdded | PluginData.addNpBlock(world.getName(),blockId);
                    } else {
                        materialRemoved = materialRemoved | PluginData.removeNpBlock(world.getName(),blockId);
                    }
                }
                if(args[0].equalsIgnoreCase("add")) {
                    if(materialAdded) {
                        sendMaterialAddedMessage(p);
                    } else {
                        sendMaterialAlreadyNpMessage(p);
                    }
                }
                if(args[0].equalsIgnoreCase("remove")) {
                    if(materialRemoved) {
                        sendMaterialRemovedMessage(p);
                    } else {
                        sendMaterialNotNpMessage(p);
                    }
                }
            } else {
                PluginData.getMessageUtil().sendInvalidSubcommandError(p);
                sendHelpMessage(p,1);
            }
        }
        return true;
    }
    
    private void sendNotEnabledErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "NoPhysicsList is not enabled for this world.");
    }

    private void sendNoValidMaterial(Player p) {
        PluginData.getMessageUtil().sendErrorMessage(p, "Not a valid block material ID.");
    }

    private void sendWorldNotFound(Player p) {
        PluginData.getMessageUtil().sendErrorMessage(p, "World not found.");
    }

    private void sendMaterialAddedMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p, "Material added to noPhysics list.");
    }

    private void sendMaterialAlreadyNpMessage(Player p) {
        PluginData.getMessageUtil().sendErrorMessage(p, "Material already is on noPhysics list.");
    }

    private void sendMaterialRemovedMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p, "Material removed from noPhysics list.");
    }

    private void sendMaterialNotNpMessage(Player p) {
        PluginData.getMessageUtil().sendErrorMessage(p, "Material is not on noPhysics list.");
    }
    
    @Override
    public String getHelpPermission() {
        return Permission.NO_PHYSICS_LIST.getPermissionNode();
    }

    @Override
    public String getShortDescription() {
        return ": Manages no physics blocks list.";
    }

    @Override
    public String getUsageDescription() {
        return ": Manages lists of blocks which will not be affected by game physics. \n "
                +ChatColor.WHITE+"Click for detailed help.";
    }
    
    @Override
    public String getHelpCommand() {
        return "/nophy help";
    }

    @Override
    protected void sendHelpMessage(Player player, int page) {
        helpHeader = "Help for "+PluginData.getMessageUtil().STRESSED+"No Physics List Editor -";
        help = new String[][]{{"/noPhy list ","<world>|-all",": Shows no physics list.","You may use '-all' instead of a worldname to show the no physics lists of all worlds."},
                                       {"/noPhy add ","<world>|-all <material>",": Adds a block"," to no physics list. Argument <material> may be a block ID (e.g. 12) or a Material name (e.g. sand)."},
                                       {"/noPhy remove "," <world>|-all <material>",": Removes a block"," from no physics list. Argument <material> may be a block ID (e.g. 12) or a Material name (e.g. sand)."}};
        super.sendHelpMessage(player, page);
    }

}
