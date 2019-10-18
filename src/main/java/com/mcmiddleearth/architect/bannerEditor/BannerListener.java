/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.bannerEditor;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.pluginutil.EventUtil;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 *
 * @author Eriol_Eandur
 */
public class BannerListener implements Listener {
    
    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.hasBlock() && player.getInventory().getItemInHand().getType().equals(Material.STICK)
                            && EventUtil.isMainHandEvent(event)) {
            BlockState state = event.getClickedBlock().getState();
            if(state instanceof Banner) {
                if(!PluginData.isModuleEnabled(player.getWorld(), Modules.BANNER_EDITOR)) {
                    sendNotEnabledErrorMessage(player);
                    return;
                }
                if(!PluginData.checkBuildPermissions(player,event.getClickedBlock().getLocation(),
                                                Permission.BANNER_EDITOR)) {
                    return;
                }
                Banner banner = (Banner) state;
                BannerEditorConfig config = BannerEditorCommand.getPlayerConfig(event.getPlayer());
                BannerEditorMode mode = config.getEditorMode();
                int patternId = config.getPatternId();
                switch(mode) {
                    case LIST:
                        sendBannerInfoMessage(player,banner);
                        break;
                    case TEXTURE:
                        if(patternId>0 
                                && patternId<=banner.numberOfPatterns()) {
                            banner.setPattern(patternId-1,
                                    new Pattern(banner.getPattern(patternId-1).getColor(),
                                                (PatternType) cycle(banner.getPattern(patternId-1).getPattern(),
                                                                    event.getAction())));
                            banner.update(true, false);
                        }
                        else if(patternId==0) {
                            sendNoPattern(player);
                        }
                        else {
                            sendInvalidPatternId(player,patternId);
                        }
                        break;
                    case COLOR:
                        if(patternId>0 
                                && patternId<=banner.numberOfPatterns()) {
                            banner.setPattern(patternId-1,
                                    new Pattern((DyeColor) cycle(banner.getPattern(patternId-1).getColor(),
                                                                 event.getAction()),
                                                banner.getPattern(patternId-1).getPattern()));
                            banner.update(true, false);
                        }
                        else if(patternId == 0) {
                            banner.setBaseColor((DyeColor) cycle(banner.getBaseColor(),
                                                                 event.getAction()));
                            banner.update(true, false);
                        }
                        else {
                            sendInvalidPatternId(player,patternId);
                        }
                        break;
                    case ADD:
                        banner.addPattern(new Pattern(DyeColor.WHITE,PatternType.CIRCLE_MIDDLE));
                        banner.update(true, false);
                        break;
                    case REMOVE:
                        if(patternId>0 
                                && patternId<=banner.numberOfPatterns()) {
                            Pattern pat = banner.removePattern(patternId-1);
                            banner.update(true, false);
                        }
                        else if(patternId==0){
                            sendBaseNoPattern(player);
                        }
                        else {
                            sendInvalidPatternId(player, patternId);
                        }
                        break;
                    case SHIELD:
                        ItemStack shieldItem = new ItemStack(Material.SHIELD);
                        BlockStateMeta shieldMeta = (BlockStateMeta) shieldItem.getItemMeta();
                        shieldMeta.setBlockState(banner);
                        shieldItem.setItemMeta(shieldMeta);
                        int amoun = shieldItem.getMaxStackSize();
                        shieldItem.setAmount(amoun);
                        player.getInventory().addItem(shieldItem);
                        sendGotShield(player, amoun);
                        break;
                    case GET:
                        ItemStack item = new ItemStack(Material.WHITE_BANNER);
                        BannerMeta meta = (BannerMeta) item.getItemMeta();
                        meta.setBaseColor(banner.getBaseColor());
                        for(Pattern pattern: banner.getPatterns()) {
                            meta.addPattern(pattern);
                        }
                        item.setItemMeta(meta);
                        int amount = item.getMaxStackSize();
                        item.setAmount(amount);
                        player.getInventory().addItem(item);
                        sendGotBanner(player, amount);
                }
                event.setCancelled(true);
            }
        }
    }

    private PatternType cycle(PatternType current, Action direction) {
        PatternType[] types = PatternType.values();
        int ordinal = current.ordinal();
        return (PatternType) cycle(types, ordinal, direction);
    }
    
    private DyeColor cycle(DyeColor current, Action direction) {
        DyeColor[] types = DyeColor.values();
        int ordinal = current.ordinal();
        return (DyeColor) cycle(types, ordinal, direction);
    }
    
    private Object cycle(Object[] types, int ordinal, Action direction) {
        if(direction.equals(Action.LEFT_CLICK_BLOCK)) {
            ordinal++;
            if(ordinal==types.length) {
                ordinal = 0;
            }
        }
        else {
            ordinal--;
            if(ordinal<0) {
                ordinal = types.length-1;
            }
        }
        return types[ordinal];
    }

    private void sendInvalidPatternId(Player player, int id) {
        PluginData.getMessageUtil().sendErrorMessage(player,"This banner doesn't have "+ id + " patterns.");
    }
    
    private void sendBannerInfoMessage(Player player, Banner banner) {
        List<Pattern> patterns = banner.getPatterns();
        PluginData.getMessageUtil().sendInfoMessage(player,"Base color (ID 0): "+ banner.getBaseColor().toString());
        int id = 1;
        for(Pattern pattern: patterns) {
            PluginData.getMessageUtil().sendInfoMessage(player,"ID "+ id+": "+ pattern.getPattern().toString()+" - "
                                           + pattern.getColor().toString());
            id++;
        }
    }
    
    private void sendNoPattern(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player,"You can change the color of the base banner only.");
    }

    private void sendBaseNoPattern(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player,"The banner base has no texture to remove.");
    }

    private void sendGotBanner(Player player, int amount) {
        PluginData.getMessageUtil().sendInfoMessage(player,"Given "+amount+" banners to "+player.getName()+".");
    }

    private void sendGotShield(Player player, int amount) {
        PluginData.getMessageUtil().sendInfoMessage(player,"Given "+amount+" shields to "+player.getName()+".");
    }

    private void sendNotEnabledErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Banner editor is not enabled for this world.");
    }
    

}