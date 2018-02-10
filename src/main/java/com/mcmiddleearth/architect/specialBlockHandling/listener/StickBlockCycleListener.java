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
package com.mcmiddleearth.architect.specialBlockHandling.listener;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialBlockInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlockItemBlock;
import com.mcmiddleearth.pluginutil.EventUtil;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class StickBlockCycleListener implements Listener {

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block block = event.getClickedBlock();
        if((event.getAction().equals(Action.RIGHT_CLICK_BLOCK )
                       || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
              && p.getInventory().getItemInMainHand().getType().equals(Material.STICK)
              && EventUtil.isMainHandEvent(event)) {
            if(!PluginData.isModuleEnabled(p.getWorld(),Modules.CYCLE_BLOCKS)) {
                sendNotEnabledErrorMessage(p);
                return;
            }   
            if(!PluginData.hasPermission(p,Permission.CYCLE_BLOCKS)) {
                PluginData.getMessageUtil().sendNoPermissionError(p);
                return;
            } else if(!PluginData.hasGafferPermission(p,block.getLocation())) {
                PluginData.getMessageUtil().sendErrorMessage(p, 
                        PluginData.getGafferProtectionMessage(p, block.getLocation()));
                return;
            }
            BlockState state = block.getState();
            int change = 1;
            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                change = -1;
            }
            switch(block.getType()) {
                case CROPS:
                case SNOW:
                case PUMPKIN_STEM:
                case MELON_STEM:
                case POTATO:
                case CARROT:
                    state.setRawData((byte)(((8+state.getRawData()+change)%8)));
                    break;
                case CAKE_BLOCK:
                    state.setRawData((byte)(((7+state.getRawData()+change)%7)));
                    break;
                    /*if(state.getRawData()>3) {
                        state.setRawData((byte)(((4+state.getRawData()+change)%4)+4));
                    } else {
                        state.setRawData((byte)(((4+state.getRawData()+change)%4)));
                    }
                    break; this was for potato and carrot in new gondor pack*/
                case BEETROOT_BLOCK:
                case CAULDRON:
                case NETHER_WARTS:
                    state.setRawData((byte)(((4+state.getRawData()+change)%4)));
                    break;
                case VINE:
                case CACTUS:
                    state.setRawData((byte)(((16+state.getRawData()+change)%16)));
                    break;
                case CHORUS_FLOWER:
                    state.setRawData((byte)(((6+state.getRawData()+change)%6)));
                    break;
                case REDSTONE_TORCH_ON:
                    state.setType(Material.REDSTONE_TORCH_OFF);
                    break;
                case REDSTONE_TORCH_OFF:
                    state.setType(Material.REDSTONE_TORCH_ON);
                    break;
                case MOB_SPAWNER:
                    //TODO: item cycling for mob spawner blocks
                    break;
                default:
                    return;
            }
//Logger.getGlobal().info("Block cycle state change! "+state.getType()+" "+state.getX()+" "+state.getY()+" "+state.getZ());
            event.setCancelled(true);
            state.update(true, false);
        }
    }

    @EventHandler
    public void cycleBlockItem(PlayerInteractEvent event) {
        if(!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) 
                || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
            return;
        }
        ArmorStand armorStand = SpecialBlockItemBlock.getArmorStand(event.getClickedBlock().getLocation());
        if(armorStand!=null
                    && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            if(PluginData.isModuleEnabled(event.getPlayer().getWorld(),Modules.SPECIAL_BLOCKS_PLACE)) {
                if(!PluginData.hasGafferPermission(event.getPlayer(),
                                                 event.getClickedBlock().getLocation())) {
                    PluginData.getMessageUtil().sendErrorMessage(event.getPlayer(), 
                            PluginData.getGafferProtectionMessage(event.getPlayer(), 
                                                 event.getClickedBlock().getLocation()));
                    event.setCancelled(true);
                    return;
                }
            }
            if(armorStand.getCustomName() != null 
                    && armorStand.getCustomName().startsWith(SpecialBlockItemBlock.PREFIX)) {
                ItemStack item = armorStand.getHelmet();
                //item.setDurability((short)((item.getDurability()+1)%Short.MAX_VALUE));
                String id = SpecialBlockItemBlock.getIdFromArmorStandName(armorStand.getCustomName());
                SpecialBlockItemBlock itemBlock 
                        = (SpecialBlockItemBlock) SpecialBlockInventoryData.getSpecialBlock(id);
                if(itemBlock==null) {
                    return;
                }
                short dura;
                if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    dura = itemBlock.getNextDurability(item.getDurability());
                } else {
                    dura = itemBlock.getPreviousDurability(item.getDurability());
                }
                item.setDurability(dura);
                armorStand.setHelmet(item);
            }
        }
    }
    
    private void sendNotEnabledErrorMessage(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "Block editor is not enabled for this world.");
    }
        

}
