/*
 * Copyright (C) 2018 Eriol_Eandur
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
package com.mcmiddleearth.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class ClientUpdateUtil {
    
    private static final Map<Material,BlockFace[]> blockPlaceUpdateSpread = new HashMap<>();
    
    private static final int maxStep = 16*16;
    
    static {
        blockPlaceUpdateSpread.put(Material.BROWN_MUSHROOM_BLOCK, new BlockFace[]{BlockFace.WEST,
                                                                                  BlockFace.EAST});
        blockPlaceUpdateSpread.put(Material.RED_MUSHROOM_BLOCK, new BlockFace[]{  BlockFace.SOUTH,
                                                                                  BlockFace.NORTH});
        blockPlaceUpdateSpread.put(Material.MUSHROOM_STEM, new BlockFace[]{  BlockFace.UP,
                                                                                  BlockFace.DOWN});
    }
    
    //private final Map<Chunk,Set<Block>> chunkQueue = new HashMap<>();
    
    //private final Set<Chunk> finshedChunks = new HashSet<>();
    
    private interface BlockChart  {
        public void add(Block block);
        public boolean contains(Block block);
    }
    
    private class BlockMap implements BlockChart {
        
        private final Map<Integer,Map<Integer,Set<Integer>>> done = new HashMap<>();
    
        @Override
        public void add(Block block) {
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            if(!done.containsKey(x)) {
                done.put(x, new HashMap<>());
            }
            Map<Integer,Set<Integer>> doneX = done.get(x);
            if(!doneX.containsKey(y)) {
                doneX.put(y, new HashSet<>());
            }
            Set<Integer> doneY = doneX.get(y);
            if(!doneY.contains(z)) {
                doneY.add(z);
            }
        }
        
        @Override
        public boolean contains(Block block) {
            int x = block.getX();
            if(!done.containsKey(x)) {
                return false;
            }
            Map<Integer,Set<Integer>> doneX = done.get(x);
            int y = block.getY();
            if(!doneX.containsKey(y)) {
                return false;
            }
            Set<Integer> doneY = doneX.get(y);
            return doneY.contains(block.getZ());
        }
    }
    
    BlockChart done;
    
    BlockFace[] faces;
    
    public void sendBlockPlaceUpdates(Block blockPlace, Player player) {
        if(blockPlaceUpdateSpread.containsKey(blockPlace.getType())) {
            sendBlockUpdate(blockPlace,player);
            Material mat = blockPlace.getType();
            for(BlockFace face: blockPlaceUpdateSpread.get(blockPlace.getType())) {
                Block block = blockPlace.getRelative(face);
                int step = 0;
                while(block.getChunk().isLoaded() && block.getType().equals(mat) 
                                                  && step < maxStep) {
                    sendBlockUpdate(block,player);
                    block = block.getRelative(face);
                    step++;
                }
            }
            /* 3D flood fill
            faces = blockPlaceUpdateSpread.get(blockPlace.getType());
            done = new BlockMap();
            sendBlockPlaceUpdates(blockPlace.getType(), blockPlace,player,0);
            
            /* -invalid
            Set<Block> start = new HashSet<>();
            start.add(blockPlace);
            chunkQueue.put(blockPlace.getChunk(), start);
            while(!chunkQueue.isEmpty()) {
                Chunk next = chunkQueue.keySet().iterator().next();
            }*/
        }
    }
    
    private void sendBlockPlaceUpdates(Material mat, Block block, Player player, int step) {
        DevUtil.log(2,"Update: "+block.getX()+" "+block.getY()+" "+block.getZ()+ " - "+step);
        if(block.getChunk().isLoaded() && !done.contains(block) 
                                       && block.getType().equals(mat) &&step < maxStep) {
            sendBlockUpdate(block, player);
            done.add(block);
            for(BlockFace face: faces) {
                sendBlockPlaceUpdates(mat, block.getRelative(face),player, step+1);
            }
        }
    }

    private void sendBlockUpdate(Block block, Player player) {
        player.sendBlockChange(block.getLocation(), block.getBlockData());
    }
    
    
}
