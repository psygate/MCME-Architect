/*
 * Copyright (C) 2018 Eriol_Eandur
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
package com.mcmiddleearth.architect.serverResoucePack;

import com.mcmiddleearth.architect.PluginData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class RpListener implements Listener{
    
    @EventHandler
    public void onRpSwitch(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        switch(event.getStatus()) {
            case SUCCESSFULLY_LOADED:
                PluginData.getMessageUtil().sendInfoMessage(player, "Resource pack loaded successfully.");
                break;
            case FAILED_DOWNLOAD:
                PluginData.getMessageUtil().sendInfoMessage(player, "Resource pack download failed. Please check your connection.");
                break;
            case DECLINED:
                PluginData.getMessageUtil().sendInfoMessage(player, "Resource pack loadig failed. Did you enable server resource packs enabled (edit server in multiplayer list)?");
                break;
        }
        RpManager.getPlayerData(player).setCurrentRpStatus(event.getStatus());
    }
}
