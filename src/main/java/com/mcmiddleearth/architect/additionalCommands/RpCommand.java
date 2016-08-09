/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.additionalCommands;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.util.DevUtil;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RpCommand extends AbstractArchitectCommand {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String c, String[] args) {
        if (!(cs instanceof Player)) {
            PluginData.getMessageUtil().sendPlayerOnlyCommandError(cs);
            return true;
        }
        if(!PluginData.hasPermission((Player)cs,Permission.RESOURCE_PACK_SWITCH)) {
            PluginData.getMessageUtil().sendNoPermissionError(cs);
            return true;
        }
        if(!PluginData.isModuleEnabled(((Player)cs).getWorld(), Modules.RESOURCE_PACK_SWITCHER)) {
            sendNotActivatedMessage(cs);
            return true;
        }
        if(args.length<1 || args[0].equalsIgnoreCase("help")) {
            int page = 1;
            if(args.length>1 && NumericUtil.isInt(args[1])) {
                page = NumericUtil.getInt(args[1]);
            }
            sendHelpMessage((Player)cs,page);
            return true;
        }
        String url = "";
        String section = "ServerResourcePacks.";
        if(args[0].toLowerCase().startsWith("e")) {
            url = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Eriador");//"http://www.mcmiddleearth.com/content/Eriador.zip";
        } 
        else if(args[0].toLowerCase().startsWith("g")) {
            url = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Gondor");//"http://www.mcmiddleearth.com/content/Gondor.zip";
        } 
        else if(args[0].toLowerCase().startsWith("l")) {
            url = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Lothlorien");//"http://www.mcmiddleearth.com/content/Lothlorien.zip";
        }
        else if(args[0].toLowerCase().startsWith("r")) {
            url = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Rohan");//"http://www.mcmiddleearth.com/content/Rohan.zip";
        }
        else if(args[0].toLowerCase().startsWith("d")) {
            url = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Dwarf");//"http://www.mcmiddleearth.com/content/Moria.zip";
        }
        else if(args[0].toLowerCase().startsWith("m")) {
            url = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Mordor");//"http://www.mcmiddleearth.com/content/Mordor.zip";
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "Resource pack not found. Current RPs are: Gondor, Rohan, Eriador, Lothlorien. Possibly Moria and Mordor are added sometime.");
    }

    private void sendNotActivatedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Resouce pack switcher is not activated for this world.");
    }
 
    @Override
    public String getHelpPermission() {
        return Permission.RESOURCE_PACK_SWITCH.getPermissionNode();
    }

    @Override
    public String getShortDescription() {
        return ": Switches resourcepacks.";
    }

    @Override
    public String getUsageDescription() {
        return " <resourcepack>: Switch to a MCME region resourcepack. Will have an effect only with server textures enabled. \n "
                +ChatColor.WHITE+"Click for details.";
    }
    
    @Override
    public String getHelpCommand() {
        return "/rp help";
    }
    
    @Override
    protected void sendHelpMessage(Player player, int page) {
        helpHeader = "Help for "+PluginData.getMessageUtil().STRESSED+"Resource Pack Switcher -";
        help = new String[][]{{"/rp e","",": Eriador"},
                                       {"/rp r","",": Rohan"},
                                       {"/rp g","",": Gondor"},
                                       {"/rp l","",": Lothlorien"},
                                       {"/rp d","",": Dwarven (Moria)"}};
        super.sendHelpMessage(player, page);
/*    private void sendHelpMessage(Player player) {
        new FancyMessage(MessageType.INFO, PluginData.getMessageUtil())
                .addSimple("Help for command: "+PluginData.getMessageUtil().STRESSED+"/rp <resourcepack>")
                .send(player);
        new FancyMessage(MessageType.INFO, PluginData.getMessageUtil())
                .addSimple("Switches to a MCME region resource pack. Make sure to have server textures enabled.")
                .send(player);
        String[][] rps = new String[][]{{"/rp e",": Eriador"},
                                       {"/rp r",": Rohan"},
                                       {"/rp g",": Gondor"},
                                       {"/rp l",": Lothlorien"},
                                       {"/rp d",": Dwarven (Moria)"}};
        for(String[] rp: rps) {
            new FancyMessage(MessageType.WHITE, PluginData.getMessageUtil())
                    .addFancy(ChatColor.DARK_AQUA+rp[0]
                                 +ChatColor.WHITE+rp[1], rp[0],"Click to switch.")
                    .send(player);
        }*/
    }
    
}
