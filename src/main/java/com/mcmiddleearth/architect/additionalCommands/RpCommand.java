/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.additionalCommands;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.MessageUtil;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RpCommand implements CommandExecutor {

    public static final String PERMISSION ="BuildFixes.resourcePack";
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String c, String[] args) {
        if (!(cs instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(cs);
            return true;
        }
        if(!PluginData.hasPermission((Player)cs,Permission.RESOURCE_PACK_SWITCH)) {
            CommonMessages.sendNoPermissionError(cs);
            return true;
        }
        if(!PluginData.isModuleEnabled(((Player)cs).getWorld(), Modules.RESOURCE_PACK_SWITCHER)) {
            sendNotActivatedMessage(cs);
            return true;
        }
        if(args.length<1) {
            CommonMessages.sendNotEnoughArgumentsError(cs);
            return true;
        }
        String url = "";
        if(args[0].toLowerCase().startsWith("e")) {
            url = "http://www.mcmiddleearth.com/content/Eriador.zip";
        } 
        else if(args[0].toLowerCase().startsWith("g")) {
            url = "http://www.mcmiddleearth.com/content/Gondor.zip";
        } 
        else if(args[0].toLowerCase().startsWith("l")) {
            url = "http://www.mcmiddleearth.com/content/Lothlorien.zip";
        }
        else if(args[0].toLowerCase().startsWith("r")) {
            url = "http://www.mcmiddleearth.com/content/Rohan.zip";
        }
        else if(args[0].toLowerCase().startsWith("mori")) {
            url = "http://www.mcmiddleearth.com/content/Moria.zip";
        }
        else if(args[0].toLowerCase().startsWith("mord")) {
            url = "http://www.mcmiddleearth.com/content/Mordor.zip";
        }
        try {
            URLConnection connection= new URL(url).openConnection();
            String type = connection.getContentType();
            if(type.startsWith("text/html")) {
                sendRPNotFoundMessage(cs);
                return true;
            }
        }
        catch(IOException e) {
            DevUtil.log(1, "IOExeption with URLconnection");
            DevUtil.log(e.toString());
        }
        ((Player)cs).setResourcePack(url);
        return true;
    }

    private void sendRPNotFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Resource pack not found. Current RPs are: Gondor, Rohan, Eriador, Lothlorien. Possibly Moria and Mordor are added sometime.");
    }

    private void sendNotActivatedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Resouce pack switcher is not activated for this world.");
    }
    
}
