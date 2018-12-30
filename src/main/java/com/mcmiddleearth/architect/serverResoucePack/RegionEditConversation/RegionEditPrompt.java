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
package com.mcmiddleearth.architect.serverResoucePack.RegionEditConversation;

import com.boydti.fawe.object.FawePlayer;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.serverResoucePack.RpManager;
import com.mcmiddleearth.architect.serverResoucePack.RpRegion;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.List;
import java.util.ListIterator;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RegionEditPrompt extends StringPrompt implements ConversationAbandonedListener{

    @Override
    public String getPromptText(ConversationContext cc) {
        RpRegion region = getRegion(cc);
        return "You are editing rp region: "+region.getName()+"\n"
             + "- weight: "+region.getWeight()+"\n"
             + "- rp: "+region.getRp()
             + "- region: "+(region.getRegion() instanceof Polygonal2DRegion?"posible to edit":"not possible to edit");
    }

    @Override
    public Prompt acceptInput(ConversationContext cc, String input) {
        String[] words = input.split(" ");
        switch(words[0]) {
            case "weight":
                if(words.length<2) {
                    return new ResponsePrompt("Missing argument.");
                }
                if(NumericUtil.isInt(words[1]) && NumericUtil.getInt(words[1])>0) {
                    getRegion(cc).setWeight(NumericUtil.getInt(words[1]));
                    RpManager.saveRpRegion(getRegion(cc));
                    return new ResponsePrompt("Region weight set to: "+words[1]);
                } else {
                    return new ResponsePrompt("Weight must be a number greater 0.");
                }
            case "rp":
                if(words.length<2) {
                    return new ResponsePrompt("Missing argument.");
                }
                String rp = RpManager.matchRpName(words[1]);
                if(!rp.equalsIgnoreCase("")) {
                    getRegion(cc).setRp(rp);
                    RpManager.saveRpRegion(getRegion(cc));
                    return new ResponsePrompt("RP changed to: "+rp);
                } else {
                    return new ResponsePrompt("No RP found for name: "+words[1]);
                }
            case "region":
                if(words.length<2) {
                    return new ResponsePrompt("Missing argument.");
                }
                if(words[1].equalsIgnoreCase("set")) {
                    Region weRegion = FawePlayer.wrap(getPlayer(cc)).getSelection();
                    if(weRegion!=null) {
                        getRegion(cc).setRegion(weRegion.clone());
                        RpManager.saveRpRegion(getRegion(cc));
                        return new ResponsePrompt("Region bounds changed to current selection.");
                    } else {
                        return new ResponsePrompt("Make a WE selection first");
                    }
                }
                if(!(getRegion(cc).getRegion() instanceof Polygonal2DRegion)) {
                    return new ResponsePrompt("You can edit polygonal regions only");
                }
                if(words.length<3) {
                    return new ResponsePrompt("Missing argument.");
                }
                if(!NumericUtil.isInt(words[2])) {
                    return new ResponsePrompt("You need to specify an index.");
                }
                int x = getPlayer(cc).getLocation().getBlockX();
                int z = getPlayer(cc).getLocation().getBlockZ();
                if(words.length>4 && NumericUtil.isInt(words[3])
                                  && NumericUtil.isInt(words[4])) {
                    x = NumericUtil.getInt(words[3]);
                    z = NumericUtil.getInt(words[4]);
                }
                RpRegion editRegion = getRegion(cc);
                switch(words[1]) {
                    case "addpoint":
                        insertPoint(editRegion,NumericUtil.getInt(words[2]),x,z);
                        break;
                    case "removepoint":
                        removePoint(editRegion,NumericUtil.getInt(words[2]));
                        break;
                    case "setpoint":
                        setPoint(editRegion,NumericUtil.getInt(words[2]),x,z);
                        break;
                    case "setmaxy":
                        ((Polygonal2DRegion)editRegion.getRegion()).setMaximumY(NumericUtil.getInt((words[2])));
                    case "setminy":
                        ((Polygonal2DRegion)editRegion.getRegion()).setMinimumY(NumericUtil.getInt((words[2])));
                    default: return new ResponsePrompt("Invalid subcommand.");
                }
                RpManager.updateDynmapRegions();
                RpManager.saveRpRegion(getRegion(cc));
                return new ResponsePrompt("Region bounds edited.");
            case "name":
                if(words.length<2) {
                    return new ResponsePrompt("Missing argument.");
                }
                RpRegion region = RpManager.getRegion(words[1]);
                if(region!=null) {
                    return new ResponsePrompt("A region with that name already exists.");
                }
                region = getRegion(cc);
                RpManager.removeRegion(region.getName());
                region.setName(words[1]);
                RpManager.saveRpRegion(region);
                RpManager.addRegion(region);
                return new ResponsePrompt("Region was renamed to: "+words[1]);
            case "quit":
                return Prompt.END_OF_CONVERSATION;
            default:
                return new ResponsePrompt("Invalid command.");
        }
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent event) {
        if(event.gracefulExit()) {
            PluginData.getMessageUtil().sendInfoMessage((Player)event.getContext()
                                                             .getSessionData("player"), 
                    "You quit from region edit conversation.");
        } else {
            PluginData.getMessageUtil().sendInfoMessage((Player)event.getContext()
                                                             .getSessionData("player"), 
                    "Region edit conversation timed out.");
        }
    }
    
    private RpRegion getRegion(ConversationContext cc) {
        return (RpRegion) cc.getSessionData("region");
    }
    
    private Player getPlayer(ConversationContext cc) {
        return (Player) cc.getSessionData("player");
    }
    
    private void insertPoint(RpRegion region, int index, int x, int z) {
        Polygonal2DRegion weRegion = (Polygonal2DRegion) region.getRegion();
        if(weRegion.size()<=index) {
            weRegion.addPoint(new BlockVector2D(x,z));
        } else {
            index = Math.max(index, 0);
            List<BlockVector2D> points = weRegion.getPoints();
            ListIterator<BlockVector2D> iterator = points.listIterator(index);
            iterator.add(new BlockVector2D(x,z));
        }
    }

    private void removePoint(RpRegion region, int index) {
        Polygonal2DRegion weRegion = (Polygonal2DRegion) region.getRegion();
        if(index >=0 && index < weRegion.size()) {
            List<BlockVector2D> points = weRegion.getPoints();
            ListIterator<BlockVector2D> iterator = points.listIterator(index);
            iterator.remove();
        }
    }

    private void setPoint(RpRegion region, int index, int x, int z) {
        Polygonal2DRegion weRegion = (Polygonal2DRegion) region.getRegion();
        if(index >=0 && index < weRegion.size()) {
            List<BlockVector2D> points = weRegion.getPoints();
            ListIterator<BlockVector2D> iterator = points.listIterator(index);
            iterator.set(new BlockVector2D(x,z));
        }
    }
}
