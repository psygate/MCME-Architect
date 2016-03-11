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
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.MessageUtil;
import com.mcmiddleearth.util.NumericUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class NoPhysicsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String c, String[] args) {
        if (!(cs instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(cs);
            return true;
        }
        if(!PluginData.hasPermission((Player)cs,Permission.NO_PHYSICS_LIST)) {
            CommonMessages.sendNoPermissionError(cs);
            return true;
        }
        if(!PluginData.isModuleEnabled(((Player)cs).getWorld(), Modules.NO_PHYSICS_LIST_ENABLED)) {
            sendNotEnabledErrorMessage(cs);
            return true;
        } else {
            Player p = (Player) cs;
            if(args.length<2) {
                CommonMessages.sendNotEnoughArgumentsError(p);
                sendHelpMessage(p);
            } else if(args[0].equalsIgnoreCase("list")) {
                MessageUtil.sendInfoMessage(p, "No physics list for");
                for (String worldName : PluginData.getWorldNames()) {
                    if (worldName.equalsIgnoreCase(args[1]) || args[1].equalsIgnoreCase("-all")) {
                        MessageUtil.sendNoPrefixInfoMessage(p, "- "+worldName+": "
                                                               + PluginData.getNpList(worldName));
                    }
                }
            } else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                if(args.length<3) {
                    CommonMessages.sendNotEnoughArgumentsError(p);
                    sendHelpMessage(p);
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
                CommonMessages.sendInvalidSubcommandError(p);
                sendHelpMessage(p);
            }
        }
        return true;
    }
    
    private void sendNotEnabledErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "NoPhysicsList is not enabled for this world.");
    }

    private void sendHelpMessage(Player p) {
        MessageUtil.sendNoPrefixInfoMessage(p, "/noPhy add|remove <World>|-all <BlockId>");
        MessageUtil.sendNoPrefixInfoMessage(p, "/noPhy list <World>|-all");
    }

    private void sendNoValidMaterial(Player p) {
        MessageUtil.sendErrorMessage(p, "Not a valid block material ID.");
    }

    private void sendWorldNotFound(Player p) {
        MessageUtil.sendErrorMessage(p, "World not found.");
    }

    private void sendMaterialAddedMessage(Player p) {
        MessageUtil.sendInfoMessage(p, "Material added to noPhysics list.");
    }

    private void sendMaterialAlreadyNpMessage(Player p) {
        MessageUtil.sendErrorMessage(p, "Material already is on noPhysics list.");
    }

    private void sendMaterialRemovedMessage(Player p) {
        MessageUtil.sendErrorMessage(p, "Material removed from noPhysics list.");
    }

    private void sendMaterialNotNpMessage(Player p) {
        MessageUtil.sendErrorMessage(p, "Material is not on noPhysics list.");
    }
    
}
