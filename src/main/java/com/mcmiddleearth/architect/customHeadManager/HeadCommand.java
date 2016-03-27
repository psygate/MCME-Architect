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
package com.mcmiddleearth.architect.customHeadManager;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.FileUtil;
import com.mcmiddleearth.util.MessageUtil;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class HeadCommand implements CommandExecutor{

    CustomHeadCollection headCollection = new CustomHeadCollection();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(sender);
            return true;
        }
        Player player = (Player) sender;
        if(!PluginData.isModuleEnabled(player.getWorld(), Modules.CUSTOM_HEAD_MANAGER)) {
            sendNotActivatedMessage(player);
            return true;
        }
        if (args.length < 1) {
            CommonMessages.sendNotEnoughArgumentsError(sender);
            return true;
        }
        
        //user commands
        if(!PluginData.hasPermission(player,Permission.CUSTOM_HEAD_USER)) {
            CommonMessages.sendNoPermissionError(player);
            return true;
        }
        if(args[0].equalsIgnoreCase("list")) {
            String[] messageArgs;
            if(args.length<2) {
                messageArgs = new String[0];
            } else {
                messageArgs = Arrays.copyOfRange(args,1,args.length);
            }
            MessageUtil.sendClickableFileListMessage(player, 
                                                     ChatColor.YELLOW+"CustomHeads /", 
                                                     CustomHeadManagerData.getAcceptedHeadDir(), 
                                                     FileUtil.getFileExtFilter(CustomHeadManagerData.getFileExtension()), 
                                                     messageArgs,
                                                     "/chead list", 
                                                     "/get head");
            return true;
        }
        if (args.length < 2 && !args[0].equalsIgnoreCase("setCollection")
                            && !args[0].equalsIgnoreCase("reviewList")
                            && !args[0].equalsIgnoreCase("reload")) {
            CommonMessages.sendNotEnoughArgumentsError(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("submit")) {
            String headName="";
            if(args.length>2) {
                UUID ownerId=null;
                try {
                    ownerId = UUID.fromString(args[1]);
                }
                catch(IllegalArgumentException e) {}
                if(ownerId==null) {
                    CustomHeadManagerData.submitHead(player, args[1], args[2]);
                } else {
                    CustomHeadManagerData.submitHead(player, ownerId, args[2]);
                }
            } else {
                CustomHeadManagerData.submitHead(player, player.getUniqueId(), args[1]);
            }
            sendSubmittingHeadMessage(player);
            return true;
        }
            
        //manager commands
        if(!PluginData.hasPermission(player,Permission.CUSTOM_HEAD_MANAGER)) {
            CommonMessages.sendNoPermissionError(player);
            return true;
        }
        if(args[0].equalsIgnoreCase("reload")) {
            CustomHeadManagerData.load();
            sendHeadsReloaded(player);
            return true;
        }
        if(args[0].equalsIgnoreCase("reviewList")) {
            String[] messageArgs;
            if(args.length<2) {
                messageArgs = new String[0];
            } else {
                messageArgs = Arrays.copyOfRange(args,1,args.length);
            }
            MessageUtil.sendClickableFileListMessage(player, 
                                                     ChatColor.GOLD+"Submitted CustomHeads /", 
                                                     CustomHeadManagerData.getSubmittedHeadDir(), 
                                                     FileUtil.getFileExtFilter(CustomHeadManagerData.getFileExtension()), 
                                                     messageArgs,
                                                     "/chead reviewList", 
                                                     "/chead review");
            return true;
        }
        if(args[0].equalsIgnoreCase("setCollection")) {
            CustomHeadGallery gallery = CustomHeadManagerData.getGallery();
            if(gallery == null) {
                CustomHeadManagerData.setGallery(
                        new CustomHeadGallery(CustomHeadManagerData.getCollection(),
                                              player.getLocation()));
                sendNewGalleryCreatedMessage(player);
            } else {
                gallery.setLocation(player.getLocation());
                sendGalleryMovedMessage(player);
            }
            CustomHeadManagerData.saveGallery();
            return true;
        }
        if(args[0].equalsIgnoreCase("review")) {
            ItemStack head = CustomHeadManagerData.getSumittedHead(args[1]);
            if(head!=null) {
                player.getInventory().addItem(head);
                sendGivenReviewHead(player);
            } else {
                sendHeadNotFound(player);
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("reject")) {
            if(CustomHeadManagerData.rejectHead(args[1])) {
                sendHeadRejected(player);
            } else {
                sendHeadNotFound(player);
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("delete")) {
            if(CustomHeadManagerData.deleteHead(args[1])) {
                sendHeadDeleted(player);
            } else {
                sendHeadNotFound(player);
            }
            return true;
        }
        if (args.length < 3) {
            CommonMessages.sendNotEnoughArgumentsError(sender);
            return true;
        }
        if(args[0].equalsIgnoreCase("rename")) {
            if(CustomHeadManagerData.getHeadData(args[1])==null) {
                sendHeadNotFound(player);
                return true;
            }
            if(CustomHeadManagerData.renameHead(args[1],args[2])) {
                sendHeadMoved(player);
            } else {
                sendHeadAlreadyExists(player);
            }
            return true;
        }
        if(args[0].equalsIgnoreCase("accept")) {
            CustomHeadData data = CustomHeadManagerData.getSubmittedHeadData(args[1]);
            if(data == null) {
                sendHeadNotFound(player);
                return true;
            }
            if(CustomHeadManagerData.acceptHead(args[1],args[2])) {
                sendHeadAcceptedMessage(player);
            } else {
                sendHeadAlreadyExists(player);
            }
            return true;
        }
        CommonMessages.sendInvalidSubcommandError(player);
        return true;
    }

    private void sendNotActivatedMessage(Player player) {
        MessageUtil.sendErrorMessage(player,"Custom Heads are not enabled for this world.");
    }

    private void sendNewGalleryCreatedMessage(Player player) {
        MessageUtil.sendInfoMessage(player,"Custom Head Gallery was created at your location.");
    }

    private void sendGalleryMovedMessage(Player player) {
        MessageUtil.sendInfoMessage(player,"Custom Head Gallery was moved to your location.");
    }

    private void sendGivenReviewHead(Player player) {
        MessageUtil.sendInfoMessage(player,"Given submitted head for review.");
    }

    private void sendHeadNotFound(Player player) {
        MessageUtil.sendErrorMessage(player,"Head not found.");
    }

    private void sendHeadAcceptedMessage(Player player) {
        MessageUtil.sendInfoMessage(player,"Head was added to Custom Head Collection.");
    }

    private void sendHeadAlreadyExists(Player player) {
        MessageUtil.sendErrorMessage(player,"Head could not be added to Custom Head Collection. Already exists?");
    }

    private void sendHeadRejected(Player player) {
        MessageUtil.sendInfoMessage(player,"Head was deleted from submitted heads directory.");
    }

    private void sendHeadDeleted(Player player) {
        MessageUtil.sendInfoMessage(player,"Head was deleted from Custom Head Collection.");
    }

    private void sendHeadMoved(Player player) {
        MessageUtil.sendInfoMessage(player,"Head was renamed.");
    }

    private void sendSubmittingHeadMessage(Player player) {
        MessageUtil.sendInfoMessage(player,"Submitting your head . . . .");
    }
    
    private void sendHeadsReloaded(Player player) {
        MessageUtil.sendInfoMessage(player,"Custom Head Collection was reloaded from file.");
    }
    
}
