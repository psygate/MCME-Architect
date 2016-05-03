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

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.MessageUtil;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class NewAfkCommand implements CommandExecutor{

    boolean flag = false; 
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(sender);
            return true;
        }
        Player player = (Player)sender;
        if(!PluginData.hasPermission(player, Permission.FULL_BRIGHTNESS)) {
            CommonMessages.sendNoPermissionError(sender);
            return true;
        }
        if(!PluginData.isAFK(player.getUniqueId())) {
            PluginData.setAFK(player.getUniqueId());
            player.setPlayerListName(player.getName()+" (AFK)");
            MessageUtil.sendInfoMessage(sender, "afk");
        } else {
            PluginData.undoAFK(player.getUniqueId());
            player.setPlayerListName(player.getName());
            MessageUtil.sendInfoMessage(sender, "wb");
        }
        /*JavaPlugin essentials = (JavaPlugin) ArchitectPlugin.getPluginInstance().getServer().getPluginManager().getPlugin("Essentials");
        if(essentials!=null) {
            if(!flag) {
                flag= true;
                Logger.getGlobal().info("Calling Essentials afk");
                essentials.getCommand("afk").execute(sender, label, args);
            } else {
                Logger.getGlobal().info("resetting flag");
                flag = false;
            }
        }*/
        return true;
    }

    private void sendNotEnabledErrorMessage(CommandSender sender) {
        MessageUtil.sendErrorMessage(sender,"Full brightness is not enabled for this world.");
    }

    private void sendOffMessage(Player p) {
        MessageUtil.sendInfoMessage(p,"AKF off!");
    }
    
    private void sendOnMessage(Player p) {
        MessageUtil.sendInfoMessage(p,"AFK on!");
    }
    
}
