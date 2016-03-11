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
package com.mcmiddleearth.architect.voxelStencilEditor;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.FileUtil;
import com.mcmiddleearth.util.MessageUtil;
import java.io.File;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class VvCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(sender);
            return true;
        }
        if(!PluginData.hasPermission((Player)sender,Permission.VOXEL_VIEWER)) {
            CommonMessages.sendNoPermissionError(sender);
            return true;
        }
        Player player = (Player) sender;
        if(!PluginData.isModuleEnabled(player.getWorld(), Modules.VOXEL_VIEWER)) {
            sendNotActivatedMessage(player);
            return true;
        }
        if(args.length<1 || args[0].toLowerCase().startsWith("list")) {
            String[] messageArgs;
            if(args.length<2) {
                messageArgs = new String[0];
            } else {
                messageArgs = Arrays.copyOfRange(args,1,args.length);
            }
            MessageUtil.sendClickableFileListMessage(player, 
                                                     ChatColor.YELLOW+"StencilLists /", 
                                                     VoxelConstants.STENCIL_LISTS_DIR, 
                                                     FileUtil.getFileExtFilter(VoxelConstants.STENCIL_LIST_EXT), 
                                                     messageArgs,
                                                     "/vv list", 
                                                     "/b sl");
        } else if(args[0].toLowerCase().startsWith("stencil")) {
            MessageUtil.sendClickableFileListMessage(player, 
                                                     ChatColor.GOLD+"Voxel Stencils /", 
                                                     VoxelConstants.STENCILS_DIR, 
                                                     FileUtil.getFileExtFilter(VoxelConstants.STENCIL_EXT), 
                                                     Arrays.copyOfRange(args,1,args.length),
                                                     "/vv stencil", 
                                                     "/b st");
        } else if(args[0].equalsIgnoreCase("delete")) {
            if(!PluginData.hasPermission((Player)sender,Permission.VOXEL_VIEWER_DELETE)) {
                CommonMessages.sendNoPermissionError(sender);
                return true;
            }
            if(args.length<2) {
                CommonMessages.sendNotEnoughArgumentsError(player);
                return true;
            }
            File file;
            if(args[1].endsWith(VoxelConstants.STENCIL_EXT)) {
                file = new File(VoxelConstants.STENCILS_DIR+"/"+args[1]);
            } else {
                file = new File(VoxelConstants.STENCIL_LISTS_DIR+"/"+args[1]);
            }
            if(!file.exists()) {
                MessageUtil.sendErrorMessage(player, "File not found.");
                return true;
            }
            if(file.isDirectory() && file.list().length>0) {
                MessageUtil.sendErrorMessage(player, "You may delete an empty directory only.");
                return true;
            }
            file.delete();
            MessageUtil.sendInfoMessage(player,"File deleted.");
        } else if(args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(player);
        } else {
            CommonMessages.sendInvalidSubcommandError(player);
        }
        return true;
    }

    private void sendNotActivatedMessage(Player player) {
        MessageUtil.sendErrorMessage(player,"Voxel viewer is not enabled.");
    }

    private void sendHelpMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Help for voxel viewer:");
        MessageUtil.sendNoPrefixInfoMessage(player, "View voxel stencils: /vv stencil [directory] [#page]");
        MessageUtil.sendNoPrefixInfoMessage(player, "View stencil lists:   /vv [list] [directory] [#page]");
        MessageUtil.sendNoPrefixInfoMessage(player, "Delete voxel files:   /vv delete filename");
    }
}

