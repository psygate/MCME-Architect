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
package com.mcmiddleearth.architect.specialBlockHandling;

/**
 *
 * @author Eriol_Eandur
 */
public enum SpecialBlockType {
    
    BLOCK         ,//("normal"),
    FOUR_DIRECTIONS,//("fourFaced"),
    FIVE_FACES     ,//("sixFaces"),
    SIX_FACES     ,//("sixFaces"),
    EIGHT_FACES,
    INVALID       ,//("invalid"),
    BURNING_FURNACE,
    THREE_AXIS,
    TWO_AXIS,
    WALL_COMBI,
    ITEM_BLOCK,
    ITEM_BLOCK_TWO_DIRECTIONS,
    DOOR,
    THIN_WALL,
    DOOR_THREE_BLOCKS,
    DOOR_FOUR_BLOCKS;//("threeAxis");
    
/*    @Getter
    private final String type;

    private SpecialBlockType(String type) {
        this.type = type;
    }*/
}