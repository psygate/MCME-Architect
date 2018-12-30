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
package com.mcmiddleearth.architect.serverResoucePack;

import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.HttpTextInputHandler;
import java.io.BufferedReader;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class _invalid_RPSwitcher extends HttpTextInputHandler{
    
    private final Player player;
    
    String url;
    
    public _invalid_RPSwitcher(String url, Player player) {
        super(url,2000);
        this.player = player;
        this.url = url;
    }

    @Override
    protected void handleTextInput(BufferedReader reader) {
        player.setResourcePack(url);
        PluginData.getMessageUtil().sendInfoMessage(player, "Resource pack switched. More info > /rp help");
    }

    @Override
    protected void sendIOException() {
        sendBadResponseError();
    }

    @Override
    protected void sendBadResponseError() {
        player.setResourcePack(url);
        PluginData.getMessageUtil().sendErrorMessage(player, "Unable to connect to server resource pack, you may see a cached one. More info > /rp help)");
    }
    
}
