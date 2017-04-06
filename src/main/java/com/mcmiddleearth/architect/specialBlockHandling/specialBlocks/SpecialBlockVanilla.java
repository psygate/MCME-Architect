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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockVanilla extends SpecialBlock {
    
    private SpecialBlockVanilla(String id, 
                        Material material, 
                        byte dataValue) {
        this(id, material, dataValue, SpecialBlockType.VANILLA);
    }
    
    protected SpecialBlockVanilla(String id, 
                        Material material, 
                        byte dataValue,
                        SpecialBlockType type) {
        super(id,material,dataValue,type);
    }
    
    public static SpecialBlockVanilla loadFromConfig(ConfigurationSection config, String id) {
            Material blockMat =  Material.AIR;
            byte data = (byte) 0;
            if(blockMat!=null) {
                return new SpecialBlockVanilla(id, blockMat, data);
            } 
            return null;
    }
    
    @Override
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Player player) {
    }
    
    @Override
    protected BlockState getBlockState(Block blockPlace, BlockFace blockFace, Location playerLoc) {
        return blockPlace.getState();
    }
    
}
