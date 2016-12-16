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
        String urlStr = PluginData.getRpUrl(PluginData.matchRpName(args[0]));//"";
        if(urlStr.equals("")) {
            sendRPNotFoundMessage(cs);
            return true;
        }
        /*String section = "ServerResourcePacks.";
        if(args[0].toLowerCase().startsWith("e")) {
            urlStr = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Eriador");//"http://www.mcmiddleearth.com/content/Eriador.zip";
        } 
        else if(args[0].toLowerCase().startsWith("g")) {
            urlStr = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Gondor");//"http://www.mcmiddleearth.com/content/Gondor.zip";
        } 
        else if(args[0].toLowerCase().startsWith("l")) {
            urlStr = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Lothlorien");//"http://www.mcmiddleearth.com/content/Lothlorien.zip";
        }
        else if(args[0].toLowerCase().startsWith("r")) {
            urlStr = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Rohan");//"http://www.mcmiddleearth.com/content/Rohan.zip";
        }
        else if(args[0].toLowerCase().startsWith("d")) {
            urlStr = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Dwarf");//"http://www.mcmiddleearth.com/content/Moria.zip";
        }
        else if(args[0].toLowerCase().startsWith("m")) {
            urlStr = ArchitectPlugin.getPluginInstance().getConfig().getString(section+"Mordor");//"http://www.mcmiddleearth.com/content/Mordor.zip";
        } else {
            sendRPNotFoundMessage(cs);
            return true;
        }*/
        new RPSwitcher(urlStr, (Player) cs).start();
        /*final String url = urlStr;
        final Player player = (Player) cs;
        final URLConnection connection = null;
        final boolean recieved = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(recieved) {
                    try {
                        String type = connection.getContentType();
                        if(type == null || type.startsWith("text/html")) {
                            sendNoConnectionToRPServer(player);
                        } else {
                            sendRPSwitchedMessage(player);
                        }
                        cancel();
                    } finally {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(ArchitectPlugin.getPluginInstance(), 10, 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    connection= new URL(url).openConnection();
                    connection.setConnectTimeout(2000);
                    String type = connection.getContentType();
                    if(type == null || type.startsWith("text/html")) {
                        sendNoConnectionToRPServer(player);
                    } else {
                        sendRPSwitchedMessage(player);
                    }
                }
                catch(IOException e) {
                    sendNoConnectionToRPServer(player);
                    DevUtil.log(1, "IOExeption with URLconnection");
                    DevUtil.log(e.toString());
                }
                player.setResourcePack(url);
            }
        }.runTaskAsynchronously(ArchitectPlugin.getPluginInstance());*/
        return true;
    }

    private void sendRPNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Resource pack not found. For more info use /rp help.");
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
                                       {"/rp d","",": Dwarven (Moria)"},
                                       {"/rp m","",": Mordor"}};
        super.sendHelpMessage(player, page);
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(player, "Enable server textures enabled to use this command: Disconnect > Edit MCME Server > Server Resource Packs: enabled");
    }
    
}
