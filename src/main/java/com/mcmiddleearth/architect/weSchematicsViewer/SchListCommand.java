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
package com.mcmiddleearth.architect.weSchematicsViewer;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.FileUtil;
import com.mcmiddleearth.util.MessageUtil;
import java.io.File;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class SchListCommand implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(sender);
            return true;
        }
        if(!PluginData.hasPermission((Player)sender,Permission.WE_SCHEMATICS_VIEWER)) {
            CommonMessages.sendNoPermissionError(sender);
            return true;
        }
        Player player = (Player) sender;
        if(!PluginData.isModuleEnabled(player.getWorld(), Modules.WE_SCHEMATICS_VIEWER)) {
            sendNotActivatedMessage(player);
            return true;
        }
        File baseDir = new File(ArchitectPlugin.getPluginInstance()
                                 .getDataFolder().getParent() + "/WorldEdit/schematics");
        MessageUtil.sendClickableFileListMessage(player, 
                                                 ChatColor.GOLD+"World Edit schematics /", 
                                                 baseDir, 
                                                 FileUtil.getFileOnlyFilter(), 
                                                 args,
                                                 "/schlist", 
                                                 null);
        return true;
    }

    private void sendNotActivatedMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "World edit schematics viewer is not enabled.");
    }
}
