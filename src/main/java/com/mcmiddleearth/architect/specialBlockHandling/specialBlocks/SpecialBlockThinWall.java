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
import com.mcmiddleearth.util.DoorUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.Door;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockThinWall extends SpecialBlockDoor {
    
    private final boolean hingeRight;
    
    private SpecialBlockThinWall(String id, 
                        Material material, boolean alternateType) {
        super(id, material, (byte) 0, SpecialBlockType.THIN_WALL);
        this.hingeRight = alternateType;
    }
    
    public static SpecialBlockThinWall loadFromConfig(ConfigurationSection config, String id) {
        Material material = matchMaterial(config.getString("blockMaterial",""));
        if(material==null) {
            return null;
        }
        boolean hingeRight = config.getBoolean("alternateType", false);
        return new SpecialBlockThinWall(id, material, hingeRight);
    }
    
    @Override
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Location playerLoc) {
        placeDoor(blockPlace, playerLoc, getMaterial(), true, true, hingeRight);
    }
    
   @Override
    public boolean matches(Block block) {
        if(getMaterial().equals(block.getType())) {
           if(DoorUtil.isLowerDoorBlock(block)) {
               block = block.getRelative(BlockFace.UP);
               if(!DoorUtil.isUpperDoorBlock(block)) {
                   return false;
               }
           }
           return hingeRight == (((Door)block.getState()).getHinge());
        }
        return false;
    }
}
