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
package com.mcmiddleearth.architect.specialBlockHandling.specialBlocks;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockType;
import com.mcmiddleearth.pluginutil.NumericUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockItemBlock extends SpecialBlock {
    
    public static final String PREFIX = "iBE_";
    public static final String ID_DELIMITER = "_id_";
    
    protected Material contentItem;
    protected Short[] contentDamage;
    private double contentHeight;
    
    private SpecialBlockItemBlock(String id, 
                        Material blockMaterial, 
                        byte blockDataValue,
                        Material contentItem,
                        Short[] contentDamage,
                        double contentHeight) {
        this(id, blockMaterial, blockDataValue, contentItem, contentDamage, contentHeight,
                SpecialBlockType.ITEM_BLOCK);
    }
    
    protected SpecialBlockItemBlock(String id, 
                        Material blockMaterial, 
                        byte blockDataValue,
                        Material contentItem,
                        Short[] contentDamage,
                        double contentHeight,
                        SpecialBlockType type) {
        super(id, blockMaterial, blockDataValue, type);
        this.contentItem = contentItem;
        this.contentDamage = contentDamage;
        this.contentHeight = contentHeight;
    }
    
    public static SpecialBlockItemBlock loadFromConfig(ConfigurationSection config, String id) {
        Material barrelMaterial = matchMaterial(config.getString("blockMaterial",""));
        if(barrelMaterial == null) {
            return null;
        }
        byte barrelData = (byte) config.getInt("blockDataValue",0);
        Material contentItem = matchMaterial(config.getString("contentItem",""));
        Short[] contentDamage = getContentDamage(config.getString("contentDamage","0"));
        double contentHeight = config.getDouble("contentHeight",0);
        return new SpecialBlockItemBlock(id, barrelMaterial, barrelData, contentItem, 
                                         contentDamage, contentHeight);
    }
    
    @Override
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Player player) {
        final Location playerLoc = player.getLocation();
        if(!PluginData.moreEntitiesAllowed(blockPlace)) {
            PluginData.getMessageUtil().sendErrorMessage(player, "Can't place. Already too many entities (paintings, item frames, item blocks and armorstands) around here.");
            return;
        }
        super.placeBlock(blockPlace, blockFace, player);
        Location loc = getArmorStandLocation(blockPlace, blockFace, playerLoc);
        removeArmorStands(blockPlace.getLocation());
        final ArmorStand armor = (ArmorStand) blockPlace.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armor.setVisible(false);
        armor.setGravity(false);
        armor.setCustomName(getArmorStandName(blockPlace)+ID_DELIMITER+getId());
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack item = new ItemStack(contentItem,1,
                                               contentDamage[NumericUtil.getRandom(0, contentDamage.length-1)]);
                armor.setHelmet(item);
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 2);
    }
    
    protected Location getArmorStandLocation(Block blockPlace, BlockFace blockFace, Location playerLoc) {
        return new Location(blockPlace.getWorld(), blockPlace.getX()+0.5, 
                                    blockPlace.getY()-2+contentHeight, blockPlace.getZ()+0.5);
    }
    
    protected static String getArmorStandName(Block blockPlace) {
        return PREFIX+blockPlace.getX()+"_"+blockPlace.getY()+"_"+blockPlace.getZ();
    }
    
    public static String getIdFromArmorStandName(String name) {
        return name.substring(name.indexOf(ID_DELIMITER)+ID_DELIMITER.length());
    }
    
    public short getNextDurability(short currentDurability) {
        for(int i=0;i<contentDamage.length;i++) {
            if(contentDamage[i]==currentDurability) {
                return ((i+1)<contentDamage.length?contentDamage[i+1]:contentDamage[0]);
            }
        }
        return contentDamage[0];
    }
    
    public short getPreviousDurability(short currentDurability) {
        for(int i=0;i<contentDamage.length;i++) {
            if(contentDamage[i]==currentDurability) {
                return ((i-1)>=0?contentDamage[i-1]:contentDamage[contentDamage.length-1]);
            }
        }
        return contentDamage[0];
    }
    
    protected static Short[] getContentDamage(String data) {
        Scanner scanner = new Scanner(data);
            scanner.useDelimiter(",");
            List<Short> contentDamageList = new ArrayList<>();
            while(scanner.hasNext()) {
                String dataValue = scanner.next();
                if(NumericUtil.isShort(dataValue)) {
                    short value = (short) NumericUtil.getShort(dataValue);
                    contentDamageList.add(value);
                }
            }
        return contentDamageList.toArray(new Short[contentDamageList.size()]);
    }
    
    public static void removeArmorStands(Location loc) {
        for(Entity entity: loc.getBlock().getWorld().getNearbyEntities(loc, 0.5, 2, 0.5)) {
            if(entity instanceof ArmorStand && entity.getCustomName()!=null
                    && entity.getCustomName().startsWith(getArmorStandName(loc.getBlock()))) {
                entity.remove();
            }
        }
    }
    
    public static ArmorStand getArmorStand(Location loc) {
        for(Entity entity: loc.getBlock().getWorld().getNearbyEntities(loc, 0.5, 2, 0.5)) {
            if(entity instanceof ArmorStand && entity.getCustomName()!=null
                    && entity.getCustomName().startsWith(getArmorStandName(loc.getBlock()))) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }
    
    @Override
    public boolean matches(Block block) {
        if(super.matches(block)) {
            ArmorStand holder = getArmorStand(block.getLocation());
            if(holder!=null) {
                ItemStack content = holder.getHelmet();
                if(content.getType().equals(contentItem)) {
                    for(short damage: contentDamage) {
                        if(damage == content.getDurability()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
