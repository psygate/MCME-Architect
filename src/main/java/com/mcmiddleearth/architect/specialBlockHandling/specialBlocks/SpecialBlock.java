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
import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockType;
import com.mcmiddleearth.pluginutil.LegacyMaterialUtil;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.util.DevUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class SpecialBlock {
    
    @Getter
    private final String id;
    
    // 1.13 removed private final Material material;
    
    @Getter
    private final BlockData blockData;
    
    // 1.13 removed private final byte dataValue;
    
    @Getter
    protected final SpecialBlockType type;
    
    private SpecialBlock(String id, 
                        //Material material, 
                        //byte dataValue,
                        BlockData data) {
        this(id, data, SpecialBlockType.BLOCK);
    }
    
    protected SpecialBlock(String id, 
                        //Material material, 
                        //byte dataValue,
                        BlockData data,
                        SpecialBlockType type) {
        this.id = id;
        //this.material = material;
        //this.dataValue = dataValue;
        this.blockData = data;
        this.type = type;
    }
    
    public static SpecialBlock loadFromConfig(ConfigurationSection config, String id) {
        BlockData data;
        //convert old data
        if(!config.contains("blockData")) {
            Material blockMat =  Material.matchMaterial(config.getString("blockMaterial",""));
            byte rawData = (byte) config.getInt("dataValue", 0);
            data = LegacyMaterialUtil.getBlockData(blockMat, rawData);
            if(data == null) {
                return null;
            }
            config.set("blockData", data.getAsString());
            config.set("blockMaterial", null);
            config.set("dataValue",null);
        // end convert old data
        }else {
            try {
                data = Bukkit.getServer().createBlockData(config.getString("blockData",""));
            } catch(IllegalArgumentException e) {
                return null;
            }
        }
        return new SpecialBlock(id, data);
    }
    
    public void placeBlock(final Block blockPlace, final BlockFace blockFace, final Player player) {
        final Location playerLoc = player.getLocation();
        final BlockState state = getBlockState(blockPlace, blockFace, playerLoc);
        new BukkitRunnable() {
            @Override
            public void run() {
                state.update(true, false);
                DevUtil.log("Special block place: ID "+state.getType()+" - DV "+state.getRawData());
                final BlockState tempState = getBlockState(blockPlace, blockFace, playerLoc);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DevUtil.log("Special block place x2: loc: "+tempState.getX()+" "+tempState.getY()+" "+tempState.getZ()+" - ID "+state.getType()+" - DV "+state.getRawData());
                        tempState.update(true, false);
                    }
                }.runTaskLater(ArchitectPlugin.getPluginInstance(), 5);
            }
        }.runTaskLater(ArchitectPlugin.getPluginInstance(), 1);
    }
    
    protected BlockState getBlockState(Block blockPlace, BlockFace blockFace, Location playerLoc) {
        final BlockState state = blockPlace.getState();
        //state.setType(material);
        //state.setRawData(dataValue);
        state.setBlockData(blockData);
        return state;
    }
    
    protected static Material matchMaterial(String identifier) {
        if(NumericUtil.isInt(identifier)) {
            return LegacyMaterialUtil.getMaterial(NumericUtil.getInt(identifier));
        } else {
            return Material.matchMaterial(identifier);
        }
    }
    
    protected static BlockFace getBlockFace(float yaw) {
        while(yaw>180) {
            yaw -=360;
        }
        while(yaw<-180) {
            yaw +=360;
        }
        if (yaw >= 135 || yaw < -135) {
            return BlockFace.NORTH;
        } else if (yaw >= 45) {
            return BlockFace.WEST;
        } else if (yaw >= -45) {
            return BlockFace.SOUTH;
        } else if (yaw >= -135) {
            return BlockFace.EAST;
        } else {
            return BlockFace.NORTH;
        }
    }
    
    protected static BlockFace getBlockFaceFine(float yaw) {
        while(yaw>180) {
            yaw -=360;
        }
        while(yaw<-180) {
            yaw +=360;
        }
        if ((yaw >= 157.5 || yaw < -157.5)) {
            return BlockFace.NORTH;
        } else if (yaw >= 112.5) {
            return BlockFace.NORTH_WEST;
        } else if (yaw >= 67.5) {
            return BlockFace.WEST;
        } else if (yaw >= 22.5) {
            return BlockFace.SOUTH_WEST;
        } else if (yaw >= -22.5) {
            return BlockFace.SOUTH;
        } else if (yaw >= -67.5) {
            return BlockFace.SOUTH_EAST;
        } else if (yaw >= -112.5) {
            return BlockFace.EAST;
        } else {
            return BlockFace.NORTH_EAST;
        }
    }
    
    /* 1.13 removed
    protected static BlockFace getOppositeBlockFace(BlockFace face) {
        switch(face) {
            case NORTH_WEST: return BlockFace.SOUTH_EAST;
            case NORTH_NORTH_WEST: return BlockFace.SOUTH_SOUTH_EAST;
            case NORTH: return BlockFace.SOUTH;
            case NORTH_NORTH_EAST: return BlockFace.SOUTH_SOUTH_WEST;
            case NORTH_EAST: return BlockFace.SOUTH_WEST;
            case EAST_NORTH_EAST: return BlockFace.WEST_SOUTH_WEST;
            case EAST: return BlockFace.WEST;
            case EAST_SOUTH_EAST: return BlockFace.WEST_NORTH_WEST;
                
            case SOUTH_WEST: return BlockFace.NORTH_EAST;
            case SOUTH_SOUTH_WEST: return BlockFace.NORTH_NORTH_EAST;
            case SOUTH: return BlockFace.NORTH;
            case SOUTH_SOUTH_EAST: return BlockFace.NORTH_NORTH_WEST;
            case SOUTH_EAST: return BlockFace.NORTH_WEST;
            case WEST_NORTH_WEST: return BlockFace.EAST_SOUTH_EAST;
            case WEST: return BlockFace.EAST;
            case WEST_SOUTH_WEST: return BlockFace.EAST_NORTH_EAST;
            case UP: return BlockFace.DOWN;
            default: return BlockFace.UP;
        }
    }*/
    
    public boolean matches(Block block) {
        return block.getBlockData().matches(blockData);
                //material.equals(block.getType())
                //&& dataValue == block.getData();
    }
    
}
