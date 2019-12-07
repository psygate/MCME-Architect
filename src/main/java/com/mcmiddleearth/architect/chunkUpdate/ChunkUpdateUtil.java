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
package com.mcmiddleearth.architect.chunkUpdate;

import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.PluginData;
//import com.mcmiddleearth.pluginutil.NMSUtil;
import com.mcmiddleearth.util.DevUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public class ChunkUpdateUtil {
    
    private final static int updateRadius = 5;
    
    //private static final Map<Material,BlockFace[]> blockPlaceUpdateSpread = new HashMap<>();
    
    private static final Map<Material,Boolean> specialUpdateMaterials = new HashMap<>();
    
    private static final int maxStep = 16*Bukkit.getServer().getViewDistance();
    private static final Set<Chunk> finishedChunks = new HashSet<>();
    private static final Set<Block> visitedBlocks = new HashSet<>();
    
    static {
        specialUpdateMaterials.put(Material.BROWN_MUSHROOM_BLOCK,false);
        specialUpdateMaterials.put(Material.RED_MUSHROOM_BLOCK,false);
        specialUpdateMaterials.put(Material.MUSHROOM_STEM,false);
        
        /*blockPlaceUpdateSpread.put(Material.BROWN_MUSHROOM_BLOCK, new BlockFace[]{BlockFace.WEST,
                                                                                  BlockFace.EAST});
        blockPlaceUpdateSpread.put(Material.RED_MUSHROOM_BLOCK, new BlockFace[]{  BlockFace.SOUTH,
                                                                                  BlockFace.NORTH});
        blockPlaceUpdateSpread.put(Material.MUSHROOM_STEM, new BlockFace[]{  BlockFace.UP,
                                                                                  BlockFace.DOWN});*/
        
    }
    
    public static synchronized void sendUpdates(Block blockPlace, Player player) {
        Location loc = blockPlace.getLocation();
        if(!PluginData.isModuleEnabled(loc.getWorld(),Modules.CHUNK_UPDATE_AUTO)) {
            return;
        }
        if(specialUpdateMaterials.containsKey(blockPlace.getType())) {
            DevUtil.log("Sending block specific chunk updates.");
            visitedBlocks.clear();
            finishedChunks.clear();
            floodFillUpdate(player, blockPlace,0,specialUpdateMaterials.get(blockPlace.getType()));
            //sendBlockUpdate(blockPlace,player);
            /*Set<Chunk> finishedChunks = new HashSet<>();
            Set<Block> visitedBlocks = new HashSet<>();
            NMSUtil.updatePlayerChunks(player, blockPlace.getLocation(), blockPlace.getLocation());
            finishedChunks.add(blockPlace.getChunk());
            Material mat = blockPlace.getType();
            for(BlockFace face: blockPlaceUpdateSpread.get(blockPlace.getType())) {
                Block block = blockPlace.getRelative(face);
                int step = 0;
                while(block.getChunk().isLoaded() && block.getType().equals(mat) 
                                                  && step < maxStep) {
                    if(!finishedChunks.contains(block.getChunk())) {
                        //sendBlockUpdate(block,player);
                        NMSUtil.updatePlayerChunks(player, block.getLocation(), block.getLocation());
                        DevUtil.log(2,"#");
                        finishedChunks.add(block.getChunk());
                    }
                    block = block.getRelative(face);
                    step++;
                }
            }*/
        } else {
            DevUtil.log("Sending local chunk updates.");
            /*NMSUtil.updatePlayerChunks(player,
                                       loc.clone().add(new Vector(-updateRadius,0,-updateRadius)), 
                                       loc.clone().add(new Vector(updateRadius,0,updateRadius)));*/
            //player.sendBlockChange(blockPlace.getLocation(), blockPlace.getBlockData());
            for(int i = -1; i<2; i++) {
                for(int j = -1; j<2; j++) {
                    Block block = blockPlace.getRelative(BlockFace.EAST,i).getRelative(BlockFace.SOUTH,j);
                    player.sendBlockChange(block.getLocation(),block.getBlockData());
                }
            }
            /*player.sendBlockChange(blockPlace.getRelative(BlockFace.EAST).getLocation(), 
                                   blockPlace.getRelative(BlockFace.EAST).getBlockData());
            player.sendBlockChange(blockPlace.getRelative(BlockFace.WEST).getLocation(), 
                                   blockPlace.getRelative(BlockFace.WEST).getBlockData());
            player.sendBlockChange(blockPlace.getRelative(BlockFace.SOUTH).getLocation(), 
                                   blockPlace.getRelative(BlockFace.SOUTH).getBlockData());
            player.sendBlockChange(blockPlace.getRelative(BlockFace.NORTH).getLocation(), 
                                   blockPlace.getRelative(BlockFace.NORTH).getBlockData());
            player.sendBlockChange(blockPlace.getRelative(BlockFace.UP).getLocation(), 
                                   blockPlace.getRelative(BlockFace.UP).getBlockData());
            player.sendBlockChange(blockPlace.getRelative(BlockFace.DOWN).getLocation(), 
                                   blockPlace.getRelative(BlockFace.DOWN).getBlockData());*/
            /*int x = loc.getChunk().getX();
            int z = loc.getChunk().getZ();
            for(int i=x-updateRadius; i<x+updateRadius; i++) {
                for(int j = z-updateRadius; j<z+updateRadius; i++) {
                    player.getWorld().refreshChunk(i, j);
                }
            }*/
        }
    }
    
    private static synchronized void floodFillUpdate(Player player, Block block, int step, boolean condition) {
        if(!(block.getBlockData() instanceof MultipleFacing) 
                || visitedBlocks.contains(block) 
                || step == maxStep) {
            return;
        }
        player.sendBlockChange(block.getLocation(), block.getBlockData());
        visitedBlocks.add(block);
        //if(!finishedChunks.contains(block.getChunk())) {
            //NMSUtil.updatePlayerChunks(player, block.getLocation(), block.getLocation());
            //player.getWorld().refreshChunk(block.getChunk().getX(), block.getChunk().getZ());
          //  finishedChunks.add(block.getChunk());
        //}
        MultipleFacing data = (MultipleFacing) block.getBlockData();
        for(BlockFace face: data.getAllowedFaces()) {
            if(data.hasFace(face) == condition) {
                Block neighbour = block.getRelative(face);
                if(neighbour.getType().equals(block.getType())) {
                    floodFillUpdate(player,neighbour,step+1,condition);
                }        
            }
        }
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
            }
        }
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
    
    
}*/
