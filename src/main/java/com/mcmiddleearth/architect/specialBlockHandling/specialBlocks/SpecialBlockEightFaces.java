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

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlockEightFaces extends SpecialBlock {
    
    private final Material[] material;
    
    private final byte[] dataValue;
    
    private SpecialBlockEightFaces(String id, 
                        Material[] material, 
                        byte[] dataValue) {
        super(id, Material.AIR, (byte) 0, SpecialBlockType.EIGHT_FACES);
        this.material = material;
        this.dataValue = dataValue;
    }
    
    public static SpecialBlockEightFaces loadFromConfig(ConfigurationSection config, String id) {
        Material material = matchMaterial(config.getString("blockMaterial",""));
        byte data = (byte) config.getInt("dataValue");
        Material[] materialAxis = new Material[8];
        byte[] dataAxis = new byte[8];
        materialAxis[0] =  matchMaterial(config.getString("blockMaterialSouth",""));
        materialAxis[1] =  matchMaterial(config.getString("blockMaterialSouthWest",""));
        materialAxis[2] =  matchMaterial(config.getString("blockMaterialWest",""));
        materialAxis[3] =  matchMaterial(config.getString("blockMaterialNorthWest",""));
        materialAxis[4] =  matchMaterial(config.getString("blockMaterialNorth",""));
        materialAxis[5] =  matchMaterial(config.getString("blockMaterialNorthEast",""));
        materialAxis[6] =  matchMaterial(config.getString("blockMaterialEast",""));
        materialAxis[7] =  matchMaterial(config.getString("blockMaterialSouthEast",""));
        for(int i=0; i<materialAxis.length;i++) {
            if(materialAxis[i]==null) {
                if(material==null) {
                    return null;
                }
                materialAxis[i]=material;
            }
        }
        dataAxis[0] = (config.isInt("dataValueSouth")?(byte) config.getInt("dataValueSouth"):data);
        dataAxis[1] = (config.isInt("dataValueSouthWest")?(byte) config.getInt("dataValueSouthWest"):data);
        dataAxis[2] = (config.isInt("dataValueWest")?(byte) config.getInt("dataValueWest"):data);
        dataAxis[3] = (config.isInt("dataValueNorthWest")?(byte) config.getInt("dataValueNorthWest"):data);
        dataAxis[4] = (config.isInt("dataValueNorth")?(byte) config.getInt("dataValueNorth"):data);
        dataAxis[5] = (config.isInt("dataValueNorthEast")?(byte) config.getInt("dataValueNorthEast"):data);
        dataAxis[6] = (config.isInt("dataValueEast")?(byte) config.getInt("dataValueEast"):data);
        dataAxis[7] = (config.isInt("dataValueSouthEast")?(byte) config.getInt("dataValueSouthEast"):data);
        return new SpecialBlockEightFaces(id, materialAxis, dataAxis);
    }
    
    @Override
    protected BlockState getBlockState(Block blockPlace, BlockFace blockFace, Location playerLoc) {
        BlockState state = blockPlace.getState();
        BlockFace blockFaceFromYaw = getBlockFaceFine(playerLoc.getYaw());
        switch(blockFaceFromYaw) {
            case SOUTH:
                state.setType(material[0]);
                state.setRawData(dataValue[0]);
                break;
            case SOUTH_WEST:
                state.setType(material[1]);
                state.setRawData(dataValue[1]);
                break;
            case WEST:
                state.setType(material[2]);
                state.setRawData(dataValue[2]);
                break;
            case NORTH_WEST:
                state.setType(material[3]);
                state.setRawData(dataValue[3]);
                break;
            case NORTH:
                state.setType(material[4]);
                state.setRawData(dataValue[4]);
                break;
            case NORTH_EAST:
                state.setType(material[5]);
                state.setRawData(dataValue[5]);
                break;
            case EAST:
                state.setType(material[6]);
                state.setRawData(dataValue[6]);
                break;
            default:
                state.setType(material[7]);
                state.setRawData(dataValue[7]);
                break;
        }
        return state;
    }
}
