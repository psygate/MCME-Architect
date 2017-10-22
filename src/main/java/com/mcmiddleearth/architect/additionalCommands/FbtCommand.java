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
package com.mcmiddleearth.architect.additionalCommands;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.additionalListeners.FbtListener;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Eriol_Eandur
 */
public class FbtCommand extends AbstractArchitectCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*Location loc = ((Player)sender).getLocation();
        int i = loc.getChunk().getX();
        int j = loc.getChunk().getZ();
        FbtListener.chunk = loc.getChunk();
        if(args.length>0 && args[0].equalsIgnoreCase("refresh")) 
            BlockState state = loc.getBlock().getState();
            state.setTypeId(64);
            state.setRawData((byte)6);
            state.update(true, false);*/
/*Logger.getGlobal().info(""+loc.getWorld().refreshChunk(i, j));
        if(args.length>0 && args[0].equalsIgnoreCase("regenerate")) 
Logger.getGlobal().info(""+loc.getWorld().regenerateChunk(i, j));
        if(args.length>0 && args[0].equalsIgnoreCase("save")) 
Logger.getGlobal().info("save "); loc.getWorld().save();
        if(args.length>0 && args[0].equalsIgnoreCase("unload")) 
Logger.getGlobal().info(""+loc.getWorld().unloadChunk(i, j, true, true));
        if(args.length>0 && args[0].equalsIgnoreCase("unloadf")) 
Logger.getGlobal().info(""+loc.getWorld().unloadChunk(i, j, true, false));
        if(args.length>0 && args[0].equalsIgnoreCase("qunload")) 
Logger.getGlobal().info(""+loc.getWorld().unloadChunkRequest(i, j, true));
        if(args.length>0 && args[0].equalsIgnoreCase("qunloadf")) 
Logger.getGlobal().info("unloadRequest "+loc.getWorld().unloadChunkRequest(i, j, false));
        if(args.length>0 && args[0].equalsIgnoreCase("load")) 
Logger.getGlobal().info("load "+loc.getWorld().loadChunk(i, j,false));*/
        if (!(sender instanceof Player)) {
            PluginData.getMessageUtil().sendPlayerOnlyCommandError(sender);
            return true;
        }
        Player player = (Player)sender;
        if(!PluginData.hasPermission(player, Permission.FULL_BRIGHTNESS)) {
            PluginData.getMessageUtil().sendNoPermissionError(sender);
            return true;
        }
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            sendOffMessage(player);
            return true;
        } else {
            if(!PluginData.isModuleEnabled(player.getWorld(), Modules.FULL_BRIGHTNESS)) {
                sendNotEnabledErrorMessage(sender);
                return true;
            }
            final PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, false, false);
            player.addPotionEffect(effect);
            sendOnMessage(player);
            return true;
        }
    }

    private void sendNotEnabledErrorMessage(CommandSender sender) {
        PluginData.getMessageUtil().sendErrorMessage(sender,"Full brightness is not enabled for this world.");
    }

    private void sendOffMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p,"Full brightness off!");
    }
    
    private void sendOnMessage(Player p) {
        PluginData.getMessageUtil().sendInfoMessage(p,"Full brightness on!");
    }

    @Override
    public String getHelpPermission() {
        return Permission.FULL_BRIGHTNESS.getPermissionNode();
    }

    @Override
    public String getShortDescription() {
        return ": Nightvision";
    }

    @Override
    public String getUsageDescription() {
        return ": Switches on/off full brightness which allows you to see in complete darkness.";
    }
    
}
