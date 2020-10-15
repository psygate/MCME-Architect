/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.blockData.attributes;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.RedstoneWire;
import org.bukkit.block.data.type.Wall;
import org.bukkit.configuration.ConfigurationSection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Eriol_Eandur
 */
public class WallAttribute extends MultiFaceAttribute {

    private Set<BlockFace> allowedFaces = new HashSet<>();

    public WallAttribute(String name) {
        super(name, Wall.class);
        allowedFaces.add(BlockFace.NORTH);
        allowedFaces.add(BlockFace.SOUTH);
        allowedFaces.add(BlockFace.WEST);
        allowedFaces.add(BlockFace.EAST);
    }

    @Override
    public int countStates() {
        return 3;
    }
    
    @Override
    public String getState() {
        return ""+ getHeight().name().toLowerCase();
    }

    @Override
    public void cycleState() {
        if(blockData instanceof Wall) {
            BlockFace face = getCurrentFace();
            Wall.Height current = getHeight();
            boolean found = false;
            for(Wall.Height search: Wall.Height.values()) {
                if(found) {
                    current = search;
                    found = false;
                    break;
                }
                if(search.equals(current)) {
                    found = true;
                }
            }
            if(found) {
                current = Wall.Height.values()[0];
            }
            ((Wall)blockData).setHeight(face, current);
        }
    }
    
    private Wall.Height getHeight() {
        if(!(blockData instanceof Wall)) {
            return Wall.Height.NONE;
        }
        BlockFace face = getCurrentFace();
        return ((Wall)blockData).getHeight(face);
    }

    @Override
    public void setState(Object newValue) {
        if(blockData instanceof Wall) {
            BlockFace face = getCurrentFace();
            ((Wall)blockData).setHeight(face, (Wall.Height) newValue);
        }
    }

    @Override
    public void loadFromConfig(ConfigurationSection config) {
        BlockFace current = getCurrentFace();
        if(config.contains(name+"_"+current.name())) {
            setState(Wall.Height.valueOf(config.getString(name+"_"+current.name())));
        } else {
            setState(Wall.Height.NONE);
        }
    }

    @Override
    public void saveToConfig(ConfigurationSection config) {
        BlockFace current = getCurrentFace();
        if(blockData instanceof Wall
                && !((Wall)blockData).getHeight(current).equals(Wall.Height.NONE)) {
            config.set(name+"_"+current.name(), ((Wall)blockData).getHeight(current).name());
        }
    }

    @Override
    protected Set<BlockFace> getAllowedFaces() {
        return allowedFaces;
    }

    @Override
    public boolean hasFace() {
        return allowedFaces.contains(getCurrentFace());
    }
}
