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
package com.mcmiddleearth.architect.noPhysicsEditor;

import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class RedstoneCircuitArea extends ExceptionArea {

    
    public RedstoneCircuitArea(CuboidRegion region) {
        super(region);
    }
    public RedstoneCircuitArea(World world, Vector minPoint, Vector maxPoint) {
        super(world,minPoint,maxPoint);
    }
    
    public RedstoneCircuitArea(UUID world, Vector minPoint, Vector maxPoint) {
        super(world,minPoint,maxPoint);
    }
    
    @Override
    public boolean isAffected(Material material) {
        return     material.equals(Material.REDSTONE_WIRE)              //55
                || material.equals(Material.REDSTONE_COMPARATOR_ON)     //150
                || material.equals(Material.REDSTONE_COMPARATOR_OFF)    //149
                || material.equals(Material.DIODE_BLOCK_OFF)            //93
                || material.equals(Material.DIODE_BLOCK_ON)             //94
                || material.equals(Material.REDSTONE_TORCH_OFF)         //75
                || material.equals(Material.REDSTONE_TORCH_ON)          //76
                || material.equals(Material.REDSTONE_LAMP_OFF)          //123
                || material.equals(Material.REDSTONE_LAMP_ON)           //124
                || material.equals(Material.PISTON_STICKY_BASE)         //29
                || material.equals(Material.PISTON_BASE)                //33
                || material.equals(Material.IRON_DOOR_BLOCK)            //71
                || material.equals(Material.WOODEN_DOOR)                //64
                || material.equals(Material.DARK_OAK_DOOR)              //197
                || material.equals(Material.SPRUCE_DOOR)                //193
                || material.equals(Material.ACACIA_DOOR)                //196
                || material.equals(Material.JUNGLE_DOOR)                //195
                || material.equals(Material.BIRCH_DOOR)                 //194
                || material.equals(Material.FENCE_GATE)                 //107
                || material.equals(Material.DARK_OAK_FENCE_GATE)        //186
                || material.equals(Material.SPRUCE_FENCE_GATE)          //183
                || material.equals(Material.ACACIA_FENCE_GATE)          //187
                || material.equals(Material.JUNGLE_FENCE_GATE)          //185
                || material.equals(Material.BIRCH_FENCE_GATE)           //183
                || material.equals(Material.TRAP_DOOR)                  //96
                || material.equals(Material.IRON_TRAPDOOR)              //167
                || material.equals(Material.DISPENSER)                  //23
                || material.equals(Material.DROPPER)                    //158
                || material.equals(Material.HOPPER)                     //154
                || material.equals(Material.ANVIL);                                  
    }
    
}
