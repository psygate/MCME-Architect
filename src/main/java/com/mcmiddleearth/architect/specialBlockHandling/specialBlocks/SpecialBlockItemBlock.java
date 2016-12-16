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

import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock;
import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockType;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockItemBlock extends SpecialBlock {
    
    public static final String PREFIX = "iBE_";
    
    private Material contentItem;
    private short contentDamage;
    private double contentHeight;
    
    private SpecialBlockItemBlock(String id, 
                        Material blockMaterial, 
                        byte blockDataValue,
                        Material contentItem,
                        short contentDamage,
                        double contentHeight) {
        this(id, blockMaterial, blockDataValue, contentItem, contentDamage, contentHeight,
                SpecialBlockType.ITEM_BLOCK);
    }
    
    protected SpecialBlockItemBlock(String id, 
                        Material blockMaterial, 
                        byte blockDataValue,
                        Material contentItem,
                        short contentDamage,
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
        short contentDamage = (short) config.getInt("contentDamage",0);
        double contentHeight = config.getDouble("contentHeight",0);
        return new SpecialBlockItemBlock(id, barrelMaterial, barrelData, contentItem, contentDamage, contentHeight);
    }
    
    @Override
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Location playerLoc) {
        super.placeBlock(blockPlace, blockFace, playerLoc);
        Location loc = getArmorStandLocation(blockPlace, blockFace, playerLoc);
        final ArmorStand armor = (ArmorStand) blockPlace.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        armor.setVisible(false);
        armor.setGravity(false);
        armor.setCustomName(PREFIX+blockPlace.getX()+"_"+blockPlace.getY()+"_"+blockPlace.getZ());
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack item = new ItemStack(contentItem,1,contentDamage);
                armor.setHelmet(item);
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 2);
    }
    
    protected Location getArmorStandLocation(Block blockPlace, BlockFace blockFace, Location playerLoc) {
        return new Location(blockPlace.getWorld(), blockPlace.getX()+0.5, 
                                    blockPlace.getY()-2+contentHeight, blockPlace.getZ()+0.5);
    }
}
