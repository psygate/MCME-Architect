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
package com.mcmiddleearth.architect;

import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public enum Modules {
    
    HALF_DOORS             ("modules.specialBlocks.halfDoors"),
    DOUBLE_SLABS           ("modules.specialBlocks.doubleSlabs"),
    PLANTS                 ("modules.specialBlocks.plants"),
    PISTON_EXTENSIONS      ("modules.specialBlocks.pistonExtenstions"),
    SIX_SIDED_LOGS         ("modules.specialBlocks.sixSidedLogs"),
    DRAGON_EGG             ("modules.specialBlocks.dragonEgg"),

    ANIMAL_SPAWN_BLOCKING  ("modules.environment.animalSpawnBlocking"),
    MONSTER_SPAWN_BLOCKING ("modules.environment.monsterSpawnBlocking"),
    FIRE_SPREAD_BLOCKING   ("modules.environment.fireSpreadBlocking"),
    WEATHER_BLOCKING       ("modules.environment.weatherBlocking"),
    DECAY_BLOCKING         ("modules.environment.decayBlocking"),
    BLOCK_FORM_BLOCKING    ("modules.environment.formBlocking"),
    NO_PHYSICS_LIST_ENABLED("modules.environment.noPhysicsListEnabled"),
    
    CUSTOM_HEAD_MANAGER    ("modules.command.customHeadManager"),
    RESOURCE_PACK_SWITCHER ("modules.command.resourcePackSwitcher"),
    FULL_BRIGHTNESS        ("modules.command.fullBrightness"),
    SPECIAL_BLOCKS         ("modules.command.getSpecialBlocks"),
    WE_SCHEMATICS_VIEWER   ("modules.command.weSchemViewer"),
    VOXEL_VIEWER           ("modules.command.voxelViewer"),
    STENCIL_LIST_EDITOR    ("modules.command.stencilListEditor"),
    BANNER_EDITOR          ("modules.command.bannerEditor"),
    PAINTING_EDITOR        ("modules.command.paintingEditor"),
    ARMOR_STAND_EDITOR     ("modules.command.armorStandEditor"),
    RANDOMISER             ("modules.command.randomiser"),
    
    VOXEL_BIOME_BRUSH_FIX  ("modules.voxelBiomeBrushFix"),
    
    ARMOR_STAND_PROTECTION    ("modules.protection.armorStand"),
    HANGING_ENTITY_PROTECTION ("modules.protection.hangingEntity");
    
    @Getter
    private final String moduleKey;

    private Modules(String key) {
        this.moduleKey = key;
    }

}
