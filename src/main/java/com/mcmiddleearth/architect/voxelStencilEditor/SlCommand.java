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

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.FileUtil;
import com.mcmiddleearth.util.MessageUtil;
import com.mcmiddleearth.util.confirmation.ConfirmationFactory;
import com.mcmiddleearth.util.confirmation.Confirmationable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class SlCommand implements CommandExecutor , Confirmationable {

    public static HashMap<UUID, StencilList> stencilLists = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(sender);
            return true;
        }
        Player player = (Player)sender;
        if(!PluginData.hasPermission(player, Permission.STENCIL_LIST_EDITOR)) {
            CommonMessages.sendNoPermissionError(sender);
            return true;
        }
        if(!PluginData.isModuleEnabled(player.getWorld(), Modules.STENCIL_LIST_EDITOR)) {
            sendNotEnabledMessage(player);
            return true;
        }
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(player);
            return false;
        }
        if (args[0].equalsIgnoreCase("save")) {
            StencilList list = stencilLists.get(player.getUniqueId());
            if(list==null) {
                sendNoCurrentStencilList(player);
                return true;
            }
            if(list.getStencils().size()<1) {
                sendNoStenicilInListMessage(player);
                return true;
            }
            if(list.fileExists()) {
                String query = "Stencil list "+ChatColor.YELLOW+list.getName()
                               +ChatColor.GOLD+" already exists. Do you want to overwrite it?";
                new ConfirmationFactory(ArchitectPlugin.getPluginInstance())
                        .start(player, query, this, new Object[]{list});
            } else {
                confirmed(player,new Object[]{list});
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("show")) {
            StencilList list = stencilLists.get(player.getUniqueId());
            if(list==null) {
                sendNoCurrentStencilList(player);
                return true;
            }
            sendShowStencilsMessage(player,list.getStencils());
            return true;
        }
        if (args.length < 2) {
            CommonMessages.sendNotEnoughArgumentsError(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            stencilLists.put(player.getUniqueId(), new StencilList(args[1]));
            sendListCreatedMessage(player, args[1]);
            return true;
        }
        if (args[0].equalsIgnoreCase("load")) {
            StencilList list =  StencilList.loadFromFile(args[1]);
            if(list==null) {
                sendStencilListNotFoundErrorMessage(player);
                return true;
            }
            stencilLists.put(player.getUniqueId(),list);
            sendListLoadedMessage(player, args[1]);
            return true;
        }
        StencilList list = stencilLists.get(player.getUniqueId());
        if(list==null) {
            sendNoCurrentStencilList(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            int count = addStencils(list, args[1]);
            sendStencilAddedMessage(player, count, list.getName());
        } else if (args[0].equalsIgnoreCase("remove")) {
            int count = removeStencils(list, args[1]);
            sendStencilsRemovedMessage(player, count, list.getName());
        } else {
            CommonMessages.sendInvalidSubcommandError(player);
        }
        return true;
    }
    
    @Override
    public void confirmed(Player player, Object[] data) {
        StencilList list = (StencilList)data[0];
        if(list.saveToFile()){
            sendListSavedMessage(player, list.getName(), list.getStencils().size());    
        } else {
            sendIOErrorSaveMessage(player);
        }
    }

    @Override
    public void cancelled(Player player, Object[] data) {
        sendSaveListCancelled(player);
    }
    
    private int addStencils(StencilList list, String stencilName) {
        File search = new File(VoxelConstants.STENCILS_DIR+"/"+stencilName);
        File dir;
        if(search.isDirectory()) {
            dir = search;
        } else {
            dir = new File(search.getParent());
        }
        File[] stencils = dir.listFiles(FileUtil.getFileExtFilter(VoxelConstants.STENCIL_EXT));
        if(stencils==null) {
            return 0;
        }
        int count = 0;
        for(File stencil: stencils) {
            String relativePath = FileUtil.getRelativePath(stencil,VoxelConstants.STENCILS_DIR);
            relativePath = relativePath.substring(0, relativePath.lastIndexOf("."));
            relativePath = relativePath.replace('\\', '/');
            String regex = stencilName.replaceAll("\\*", ".*");
            if(relativePath.matches(regex)) {
                if(list.addStencil(relativePath)) {
                    count ++;
                }
            }
        }
        return count;
    }
    
    private int removeStencils(StencilList list, String stencilName) {
        List<String> removeList = new ArrayList<>();
        int count=0;
        for(String search: list.getStencils()) {
            if(search.matches(stencilName.replaceAll("\\*", ".*"))) {
                removeList.add(search);
                count++;
            }
        }
        list.getStencils().removeAll(removeList);
        return count;
    }

    private void sendNotEnabledMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "Stencil List Editor is not enabled for this world.");
    }

    private void sendNoCurrentStencilList(Player player) {
        MessageUtil.sendErrorMessage(player,"You have to create or load a stencil list first.");
    }

    private void sendListCreatedMessage(Player player, String name) {
        MessageUtil.sendInfoMessage(player,"You created a new stencil list: "+MessageUtil.STRESSED+name);
        MessageUtil.sendNoPrefixInfoMessage(player, "You can now add or remove stencils.");
    }

    private void sendStencilListNotFoundErrorMessage(Player player) {
        MessageUtil.sendErrorMessage(player,"Stencil list not found.");
    }

    private void sendListLoadedMessage(Player player, String name) {
        MessageUtil.sendInfoMessage(player,"Stencil list was loaded from file: "+MessageUtil.STRESSED+name);
        MessageUtil.sendNoPrefixInfoMessage(player, "You can now add or remove stencils.");
    }

    private void sendHelpMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Help for stencil list editor:");
        MessageUtil.sendNoPrefixInfoMessage(player, "Create new stencil list:   /sl create <listName>");
        MessageUtil.sendNoPrefixInfoMessage(player, "Load stencil list:             /sl load <listName>");
        MessageUtil.sendNoPrefixInfoMessage(player, "Add stencil to list:         /sl add <stencilName>");
        MessageUtil.sendNoPrefixInfoMessage(player, "Remove stencil from list: /sl remove <stencilName>");
        MessageUtil.sendNoPrefixInfoMessage(player, "Save stencil list:             /sl save <listName>");
        MessageUtil.sendNoPrefixInfoMessage(player, "Show stencils in list:       /sl show");
    }

    private void sendStencilAddedMessage(Player player, int count, String name) {
        if(count <1) {
            sendNoStencilsFoundMessage(player);
            return;
        }
        String stencils;
        if(count >1) {
            stencils = "stencils were";
        } else {
            stencils = "stencil was";
        }
        MessageUtil.sendInfoMessage(player, ""+MessageUtil.STRESSED+count
                                           +MessageUtil.INFO+" "+stencils+" added "
                                           +"to stencil list " 
                                           +MessageUtil.STRESSED+name);
    }

    private void sendStencilsRemovedMessage(Player player, int count, String name) {
        if(count <1) {
            sendNoStencilsFoundMessage(player);
            return;
        }
        String stencils;
        if(count >1) {
            stencils = "stencils were";
        } else {
            stencils = "stencil was";
        }
        MessageUtil.sendInfoMessage(player, ""+MessageUtil.STRESSED+count
                                           +MessageUtil.INFO+" "+stencils+" removed "
                                           +"from stencil list " 
                                           +MessageUtil.STRESSED+name);
    }

    private void sendNoStencilsFoundMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "No stencils found.");
    }

    private void sendListSavedMessage(Player player, String name, int size) {
        MessageUtil.sendInfoMessage(player,"The stencil list "+MessageUtil.STRESSED
                                           +name+MessageUtil.INFO+" has been saved with "
                                           +MessageUtil.STRESSED+size+MessageUtil.INFO
                                           +" stencil"+(size==1?"":"s")+".");
    }

    private void sendNoStenicilInListMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Add at least one stencil to your list first.");
    }

    private void sendSaveListCancelled(Player player) {
        MessageUtil.sendInfoMessage(player, "You cancelled saving of your stencil list.");
    }

    private void sendShowStencilsMessage(Player player, List<String> list) {
        if(list.isEmpty()) {
            MessageUtil.sendInfoMessage(player,"No stencils in your list.");
            return;
        }
        MessageUtil.sendInfoMessage(player, "Stencils in your current stencil list:");
        for(String string: list) {
            MessageUtil.sendNoPrefixInfoMessage(player, "- "+string);
        }
    }

    private void sendIOErrorSaveMessage(Player player) {
        MessageUtil.sendErrorMessage(player,"There was an error while saving.");
    }

}
