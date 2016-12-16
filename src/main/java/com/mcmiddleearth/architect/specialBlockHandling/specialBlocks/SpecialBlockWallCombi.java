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
import com.mcmiddleearth.architect.specialBlockHandling.specialBlocks.SpecialBlock;
import java.util.logging.Logger;
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
public class SpecialBlockWallCombi extends SpecialBlock {
    
    private final Material[] material;
    
    private final byte[] dataValue;
    
    private SpecialBlockWallCombi(String id, 
                        Material[] material, 
                        byte[] dataValue) {
        super(id, Material.AIR, (byte) 0, SpecialBlockType.WALL_COMBI);
        this.material = material;
        this.dataValue = dataValue;
    }
    
    public static SpecialBlockWallCombi loadFromConfig(ConfigurationSection config, String id) {
        Material material = matchMaterial(config.getString("blockMaterial",""));
        byte data = (byte) config.getInt("dataValue");
        Material[] materialAxis = new Material[4];
        byte[] dataAxis = new byte[4];
        materialAxis[0] =  matchMaterial(config.getString("blockMaterialNorthWest",""));
        materialAxis[1] =  matchMaterial(config.getString("blockMaterialSouthWest",""));
        materialAxis[2] =  matchMaterial(config.getString("blockMaterialNorthEast",""));
        materialAxis[3] =  matchMaterial(config.getString("blockMaterialSouthEast",""));
        for(int i=0; i<materialAxis.length;i++) {
            if(materialAxis[i]==null) {
                if(material==null) {
                    return null;
                }
                materialAxis[i]=material;
            }
        }
        dataAxis[0] = (config.isInt("dataValueNorthWest")?(byte) config.getInt("dataValueNorthWest"):data);
        dataAxis[1] = (config.isInt("dataValueSouthWest")?(byte) config.getInt("dataValueSouthWest"):data);
        dataAxis[2] = (config.isInt("dataValueNorthEast")?(byte) config.getInt("dataValueNorthEast"):data);
        dataAxis[3] = (config.isInt("dataValueSouthEast")?(byte) config.getInt("dataValueSouthEast"):data);
        return new SpecialBlockWallCombi(id, materialAxis, dataAxis);
    }
    
    @Override
    public BlockState getBlockState(Block blockPlace, BlockFace blockFace, Location playerLoc) {
        final BlockState state = blockPlace.getState();
        float yaw = playerLoc.getYaw();
        while(yaw>180)  yaw -= 360;
        while(yaw<-180) yaw += 360;
        if(yaw > 90) {
            if(blockFace.equals(BlockFace.UP)) {
                state.setType(material[3]);
                state.setRawData(dataValue[3]);
            } else {
                state.setType(material[0]);
                state.setRawData(dataValue[0]);
            }
        } else if(yaw > 0) {
            if(blockFace.equals(BlockFace.UP)) {
                state.setType(material[2]);
                state.setRawData(dataValue[2]);
            } else {
                state.setType(material[1]);
                state.setRawData(dataValue[1]);
            }
        } else if(yaw > -90) {
            if(blockFace.equals(BlockFace.UP)) {
                state.setType(material[0]);
                state.setRawData(dataValue[0]);
            } else {
                state.setType(material[3]);
                state.setRawData(dataValue[3]);
            }
        } else {
            if(blockFace.equals(BlockFace.UP)) {
                state.setType(material[1]);
                state.setRawData(dataValue[1]);
            } else {
                state.setType(material[2]);
                state.setRawData(dataValue[2]);
            }
        }
        return state;
    }
}
