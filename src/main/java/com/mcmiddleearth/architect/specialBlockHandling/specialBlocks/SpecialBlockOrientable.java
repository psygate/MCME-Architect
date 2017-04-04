/*
 * Copyright (C) 2017 MCME
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
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class SpecialBlockOrientable extends SpecialBlock {
    
    protected Material[] material;
    
    protected byte[] dataValue;
    
    protected SpecialBlockOrientable(String id, 
                        Material material, 
                        byte dataValue,
                        SpecialBlockType type) {
        super(id,material,dataValue,type);
    }
    
    @Override
    public boolean matches(Block block) {
        for(Material mat: material) {
            if(mat.equals(block.getType())) {
                for(byte data: dataValue) {
                    if(data == block.getData()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}
