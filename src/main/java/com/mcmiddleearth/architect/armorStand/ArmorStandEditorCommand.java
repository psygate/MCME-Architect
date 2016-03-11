/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.armorStand;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.FileUtil;
import com.mcmiddleearth.util.MessageUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class ArmorStandEditorCommand implements CommandExecutor {

    private final static Map<UUID, ArmorStandEditorConfig> configList = new HashMap<>();
    
    private final int maxStepSize = 100;
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String c, String[] args) {
        if (!(cs instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(cs);
            return true;
        }
        if(!PluginData.hasPermission((Player)cs,Permission.ARMOR_STAND_EDITOR)) {
            CommonMessages.sendNoPermissionError(cs);
            return true;
        } else {
            Player p = (Player) cs;
            if(!PluginData.isModuleEnabled(p.getWorld(), Modules.ARMOR_STAND_EDITOR)) {
                sendNotActivatedMessage(cs);
                return true;
            }
            ArmorStandEditorConfig playerConfig =  getPlayerConfig(p);
            if(args.length<1) {
                sendInfoMessage(cs, playerConfig);
            }
            else {
                if(args[0].equalsIgnoreCase("help")) {
                    sendHelpMessage(cs);
                    return true;
                }
                if(args[0].equals("+") || args[0].equals("-")) {
                    int stepInDegree = playerConfig.getRotationStep();
                    if(args[0].equals("+") && stepInDegree<maxStepSize) {
                        stepInDegree += 1;
                    }
                    else if(args[0].equals("-") && stepInDegree>2) {
                        stepInDegree -= 1;
                    }
                    playerConfig.setRotationStep(stepInDegree);
                    sendRotationStepMessage(cs,playerConfig.getRotationStep());
                    return true;
                }
                if(args[0].equalsIgnoreCase("parts")) {
                    sendPartsHelpMessage(cs);
                    return true;
                }
                if(args[0].equalsIgnoreCase("place")) {
                    playerConfig.placeArmorStand(p.getLocation(),true);
                    return true;
                }
                if(args[0].equalsIgnoreCase("files")) {
                    MessageUtil.sendClickableFileListMessage(p,
                                    ChatColor.YELLOW+"Armor Stands /",
                                    ArmorStandEditorConfig.getDataDir(),
                                    FileUtil.getFileExtFilter(ArmorStandEditorConfig.getFileExtension()),
                                    Arrays.copyOfRange(args, 1, args.length), 
                                    "/armor files", 
                                    "/armor p");
                    return true;
                }
                if(args[0].equalsIgnoreCase("delete")) {
                    if(args.length>1) {
                        if(playerConfig.deleteFile(args[1])) {
                            sendFileDeletedMessage(cs);
                        }
                        else {
                            sendDeleteErrorMessage(cs);
                        }
                    }
                    return true;
                }
                if(args[0].equalsIgnoreCase("save")) {
                    if(args.length>2) {
                        String description = args[2];
                        for(int i = 3;i<args.length;i++) {
                            description = description + " " + args[i];
                        }
                        try {
                            if(playerConfig.saveArmorStand(args[1],description)) {
                                sendSavedMessage(cs);
                            }
                            else {
                                sendExistsMessage(cs);
                            }
                        } catch (IOException ex) {
                            sendIOErrorMessage(cs);
                        }
                    }
                    else
                    {
                        sendNotEnoughArgumentsMessage(cs);
                    }
                    return true;
                }
                if(args[0].equalsIgnoreCase("clear")) {
                    playerConfig.clearCopiedArmorStand();
                    sendCopiedArmorStandClearedMessage(cs);
                    return true;
                }
                try {
                    int step = Integer.parseInt(args[0]);
                    if(step>maxStepSize) step = maxStepSize;
                    if(step<1) step = 1;
                    playerConfig.setRotationStep(step);
                    sendRotationStepMessage(cs,playerConfig.getRotationStep());
                    return true;
                }
                catch(NumberFormatException e) {}
                ArmorStandEditorMode editorMode = ArmorStandEditorMode.getEditorMode(args[0]);
                if(editorMode == null) {
                    CommonMessages.sendInvalidSubcommandError(cs);
                }
                else {
                    if(editorMode.equals(ArmorStandEditorMode.PASTE) && args.length>1) {
                        try {
                            if(playerConfig.loadArmorStand(args[1])) {
                                sendLoadedMessage(cs);
                            }
                            else {
                                CommonMessages.sendFileNotFoundError(cs);
                            }
                        } catch (IOException | InvalidConfigurationException ex) {
                            sendIOErrorMessage(cs);
                        }
                    }
                    playerConfig.setEditorMode(editorMode);
                    if(args.length>1) {
                        ArmorStandPart part = ArmorStandPart.getPart(args[1]);
                        if(part!=null) {
                            playerConfig.setPart(part);
                        }
                    }
                    sendInfoMessage(cs, playerConfig);
                }
            }
            return true;
        }
    }
    
    public static ArmorStandEditorConfig getPlayerConfig(OfflinePlayer p) {
        for(UUID search: configList.keySet()) {
            if(search.equals(p.getUniqueId())) {
                return configList.get(search);
            }
        }
        ArmorStandEditorConfig newConfig = new ArmorStandEditorConfig();
        configList.put(p.getUniqueId(), newConfig);
        return newConfig;
    }
        
    private void showFiles(CommandSender cs, ArmorStandEditorConfig playerConfig, String folder, int page) {
        File[] files = playerConfig.getFiles(folder);
        int maxPage=(files.length-1)/10+1;
        if(maxPage<1) {
            maxPage = 1;
        }
        if(page>maxPage) {
            page = maxPage;
        }
        sendHeaderMessage(cs, folder, page, maxPage);
        if(!folder.equals("")) {
            MessageUtil.sendClickableMessage((Player) cs, "   ..","/armor files");
        }
        for(int i = (page-1)*10; i<files.length && i<(page-1)*10+10;i++) {
                //backward order: int i = files.length-1-(page-1)*10; i >= 0 && i > files.length-1-(page-1)*10-10; i--) {
            sendEntryMessage(cs, folder, files[i], playerConfig.getDescription(files[i]));
        }
    }
    
    private void sendHeaderMessage(CommandSender cs, String folder, int page, int maxPage) {
        MessageUtil.sendInfoMessage(cs, "Saved armor stand files "
                       +(!folder.equals("")?"in folder "+ folder+" ":"")+"[page " +page+"/"+maxPage+"]");
    }

    private void sendEntryMessage(CommandSender cs, String folder, File file, String description) {
        String name;
        String command;
        if(file.isDirectory()) {
            name = file.getName();
            command = "/armor files "+folder+"/"+name;
        }
        else {
            name = file.getName().substring(0, file.getName().lastIndexOf('.'));
            command = "/armor p "+folder+"/"+name;
        }
        while(name.length()<15) {
            name = name.concat(" ");
        }
        MessageUtil.sendClickableMessage((Player)cs, "   "+name+description, command);
    }
    
    private void sendHelpMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Tool for editing armor stands:");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Show the current mode:    /armor");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Show help about parts:    /armor parts");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select x-movement mode:   /armor mx");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select y-movement mode:   /armor my");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select z-movement mode:   /armor mz");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select move left/right:   /armor mo");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select turn mode:         /armor t");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select x-rotation mode:   /armor x [part]");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select y-rotation mode:   /armor y [part]");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select z-rotation mode:   /armor z [part]");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select view rotation mode:/armor r [part]");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Switch size mode:         /armor s");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Switch visibility mode:   /armor v");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Switch marker mode:       /armor ma");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Switch arms mode:         /armor a");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Switch base plate mode:   /armor b");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select paste mode:        /armor p [filename]");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Select copy mode:         /armor c");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Increase rot/move step:   /armor +");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Decrease rot/move step:   /armor -");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Stet rot/move step:       /armor #");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Place copied armor stand: /armor place");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Clear copied armor stand: /armor clear");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- Save copied armor stand:  /armor save <filename> <description>");
        MessageUtil.sendNoPrefixInfoMessage(cs, "- List saved armor stand:   /armor files [folder]");
    }
    
    private void sendInfoMessage(CommandSender cs, ArmorStandEditorConfig playerConfig) {
                    MessageUtil.sendInfoMessage(cs, "armor stand editor mode: ");
                    switch(playerConfig.getEditorMode()) {
                        case HAND:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> remove/place item in hand");
                            break;
                        case XROTATE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> rotate " + playerConfig.getPart().getPartName()+" along x-Axis");
                            break;
                        case YROTATE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> rotate " + playerConfig.getPart().getPartName()+" along y-axis");
                            break;
                        case ZROTATE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> rotate " + playerConfig.getPart().getPartName()+" along z-axis");
                            break;
                        case ROTATE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> rotate " + playerConfig.getPart().getPartName()+" along your view direction");
                            break;
                        case MOVE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> move to left/right");
                            break;
                        case TURN:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> turn full armor stand");
                            break;
                        case XMOVE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> move along x-axis");
                            break;
                        case YMOVE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> move along y-axis");
                            break;
                        case ZMOVE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> move along z-axis");
                            break;
                        case SIZE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> switch size");
                            break;
                        case VISIBLE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> switch visibility");
                            break;
                        case BASE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> switch base plate");
                            break;
                        case MARKER:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> switch collision box");
                            break;
                        case ARMS:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> switch arms");
                            break;
                        case PASTE:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> paste armor stand");
                            break;
                        case COPY:
                            MessageUtil.sendNoPrefixInfoMessage(cs, "   -> copy armor stand");
                            break;
                    }
    }

    private void sendRotationStepMessage(CommandSender cs, int rotationStep) {
        MessageUtil.sendInfoMessage(cs, "    -> Set rot/move step to "+rotationStep+"degree/percent");
    }

    private void sendCopiedArmorStandClearedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "armor stand clippboard was cleared.");
    }

    private void sendPartsHelpMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "parts arguments of an armor stand:");
        MessageUtil.sendNoPrefixInfoMessage(cs, "    -> h  head");
        MessageUtil.sendNoPrefixInfoMessage(cs, "    -> la  left arm");
        MessageUtil.sendNoPrefixInfoMessage(cs, "    -> ra  right arm");
        MessageUtil.sendNoPrefixInfoMessage(cs, "    -> ll  left leg");
        MessageUtil.sendNoPrefixInfoMessage(cs, "    -> rl  right leg");
        MessageUtil.sendNoPrefixInfoMessage(cs, "    -> b   body");
    }

    private void sendNotEnoughArgumentsMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Not enough arguments: /armor save <filename> <description>");
    }

    private void sendSavedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Armor stand saved.");
    }

    private void sendLoadedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Armor stand loaded.");
    }

    private void sendExistsMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "File already exists. Delete first.");
    }

    private void sendIOErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "IO error. Nothing was saved.");
    }

    private void sendFileDeletedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "File deleted.");
    }

    private void sendDeleteErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "File not found or directory not empty.");
    }

    private void sendNotActivatedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Armor stand editor is not activated for this world.");
    }
        

}
