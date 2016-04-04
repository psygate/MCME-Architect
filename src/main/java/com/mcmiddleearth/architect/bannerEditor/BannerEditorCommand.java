/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.bannerEditor;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.armorStand.ArmorStandEditorConfig;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.FileUtil;
import com.mcmiddleearth.util.MessageUtil;
import com.mcmiddleearth.util.NumericUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class BannerEditorCommand implements CommandExecutor {

    private final static Map<UUID, BannerEditorConfig> configList = new HashMap<>();
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String c, String[] args) {
        if (!(cs instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(cs);
            return true;
        }
        if(!PluginData.hasPermission((Player)cs,Permission.BANNER_EDITOR)) {
            CommonMessages.sendNoPermissionError(cs);
            return true;
        }
        if(!PluginData.isModuleEnabled(((Player)cs).getWorld(), Modules.BANNER_EDITOR)) {
            sendNotEnabledErrorMessage(cs);
            return true;
        } else {
            Player p = (Player) cs;
            BannerEditorConfig playerConfig =  getPlayerConfig(p);
            if(args.length<1) {
                sendInfoMessage(cs, playerConfig);
            }
            else {
                if(args[0].equalsIgnoreCase("help")) {
                    sendHelpMessage(cs);
                    return true;
                } else if(args[0].equalsIgnoreCase("save")) {
                    if(args.length<3) {
                        CommonMessages.sendNotEnoughArgumentsError(cs);
                        return true;
                    }
                    if(!p.getItemInHand().getType().equals(Material.BANNER)) {
                        sendNoBannerInHandError(cs);
                    } else {
                        String description = "";
                        for(int i=2;i<args.length;i++) {
                            description = description + args[i]+" ";
                        }
                        try {
                            if(playerConfig.saveBanner(p.getItemInHand(), args[1], description)) {
                                sendBannerSavedMessage(cs);
                            } else {
                                sendFileExistsMessage(cs);
                            }
                        } catch (IOException ex) {
                            CommonMessages.sendIOError(cs);
                            Logger.getLogger(BannerEditorCommand.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    return true;
                } else if(args[0].equalsIgnoreCase("load")) {
                    if(args.length<2) {
                        CommonMessages.sendNotEnoughArgumentsError(cs);
                        return true;
                    }
                    ItemStack item = playerConfig.loadBanner(args[1]);
                    if(item == null) {
                        sendBannerNotFound(cs);
                        return true;
                    }
                    item.setAmount(item.getMaxStackSize());
                    p.getInventory().addItem(item);
                    sendGotBanner(p);
                    return true;
                } else if(args[0].equalsIgnoreCase("files")) {
                    MessageUtil.sendClickableFileListMessage(p,
                                    ChatColor.YELLOW+"Banners /",
                                    BannerEditorConfig.getDataDir(),
                                    FileUtil.getFileExtFilter(BannerEditorConfig.getFileExtension()),
                                    Arrays.copyOfRange(args, 1, args.length), 
                                    "/banner files", 
                                    "/banner load");
                    return true;
                }
                int id = NumericUtil.getInt(args[0]);
                if(id>-1) {
                    playerConfig.setPatternId(id);
                    sendInfoMessage(cs, playerConfig);
                    return true;
                }
                if(args.length>1) {
                    id = NumericUtil.getInt(args[1]);
                    if(id>-1) {
                        playerConfig.setPatternId(id);
                    }
                }
                BannerEditorMode editorMode = BannerEditorMode.getEditorMode(args[0]);
                if(editorMode == null) {
                    CommonMessages.sendInvalidSubcommandError(cs);
                }
                else {
                    playerConfig.setEditorMode(editorMode);
                    sendInfoMessage(cs, playerConfig);
                }
            }
            return true;
        }
    }
    
    public static BannerEditorConfig getPlayerConfig(OfflinePlayer p) {
        for(UUID search: configList.keySet()) {
            if(search.equals(p.getUniqueId())) {
                return configList.get(search);
            }
        }
        BannerEditorConfig newConfig = new BannerEditorConfig();
        configList.put(p.getUniqueId(), newConfig);
        return newConfig;
    }
        
    private void sendHelpMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"Tool for editing banners:");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Select edited pattern:          /banner <patternId>");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Select change texture mode: /banner t");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Select change color mode:    /banner c");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Select add pattern mode:     /banner a");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Select remove pattern mode: /banner r");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Select list patterns mode:    /banner l");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Select get banner mode:      /banner g");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Save banner in hand to file: /banner save <filename> <description>");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Load banner from file:    /banner load <filename>");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Show saved banners:    /banner files [subdirectory] [#page]");
    }
    
    private void sendInfoMessage(CommandSender cs, BannerEditorConfig playerConfig) {
                    MessageUtil.sendInfoMessage(cs,"banner editor mode: ");
                    switch(playerConfig.getEditorMode()) {
                        case LIST:
                            MessageUtil.sendNoPrefixInfoMessage(cs,"   -> list patterns");
                            break;
                        case TEXTURE:
                            MessageUtil.sendNoPrefixInfoMessage(cs,"   -> change texture "+ playerConfig.getPatternId());
                            break;
                        case COLOR:
                            MessageUtil.sendNoPrefixInfoMessage(cs,"   -> change color of pattern "+ playerConfig.getPatternId());
                            break;
                        case ADD:
                            MessageUtil.sendNoPrefixInfoMessage(cs,"   -> add pattern");
                            break;
                        case REMOVE:
                            MessageUtil.sendNoPrefixInfoMessage(cs,"   -> remove pattern "+ playerConfig.getPatternId());
                            break;
                        case GET:
                            MessageUtil.sendNoPrefixInfoMessage(cs,"   -> get banner ");
                            break;
                    }
    }

    private void sendNotEnabledErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Banner editor is not enabled for this world.");
    }

    private void sendNoBannerInHandError(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You don't have a banner in hand.");
    }

    private void sendBannerSavedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Banner was saved to file.");
    }

    private void sendFileExistsMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "File already exists. Delete first.");
    }

    private void sendGotBanner(Player p) {
        MessageUtil.sendInfoMessage(p, "Banner was loaded and placed in your inventory.");
    }

    private void sendBannerNotFound(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Banner file not found or no valid banner data in file.");
    }
    
}
