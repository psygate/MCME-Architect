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

import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockType;
import static com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock.matchMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockDoubleY extends SpecialBlock{
    
   private final Material lowerMaterial;
   private final Material upperMaterial;
   
   private final byte lowerData;
   private final byte upperData;
    
    private SpecialBlockDoubleY(String id, 
                        Material lowerMaterial, Material upperMaterial,
                        byte lowerData, byte upperData) {
        super(id, Material.AIR, (byte) 0, SpecialBlockType.DOOR);
        this.lowerMaterial = lowerMaterial;
        this.upperMaterial = upperMaterial;
        this.lowerData = lowerData;
        this.upperData = upperData;
    }
    
    private SpecialBlockDoubleY(String id, 
                        Material lowerMaterial, Material upperMaterial) {
        super(id, Material.AIR, (byte) 0, SpecialBlockType.DOOR);
        this.lowerMaterial = lowerMaterial;
        this.upperMaterial = upperMaterial;
        this.lowerData = 0;
        this.upperData = 0;
    }
    
    public static SpecialBlockDoubleY loadFromConfig(ConfigurationSection config, String id) {
        Material lowerMaterial = matchMaterial(config.getString("lowerMaterial",""));
        Material upperMaterial = matchMaterial(config.getString("lowerMaterial",""));
        if(lowerMaterial==null || upperMaterial==null) {
            return null;
        }
        byte lowerData = (config.isInt("lowerDataValue")?(byte) config.getInt("lowerDataValue"):0);
        byte upperData = (config.isInt("upperDataValue")?(byte) config.getInt("upperDataValue"):0);
        return new SpecialBlockDoubleY(id, lowerMaterial, upperMaterial, lowerData, upperData);
    }
    
    @Override
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Location playerLoc) {
        super.placeBlock(blockPlace, BlockFace.DOWN,playerLoc);
        Block upper = blockPlace.getRelative(BlockFace.UP);
        if(upper.isEmpty()) {
            super.placeBlock(upper, BlockFace.UP,playerLoc);
        }
    }
    
    @Override
    protected BlockState getBlockState(Block blockPlace, BlockFace blockFace, Location playerLoc) {
        final BlockState state = blockPlace.getState();
        if(blockFace==BlockFace.UP) {
            state.setType(upperMaterial);
            state.setRawData(upperData);
        } else {
            state.setType(lowerMaterial);
            state.setRawData(lowerData);
        }
        return state;
    }
    
}
