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
public enum Permission {
    
    ARCHITECT_INFO        ("architect.info"),
    ARCHITECT_RELOAD      ("architect.reload"),
    FULL_BRIGHTNESS       ("architect.fullBrightness"),
    RESOURCE_PACK_SWITCH  ("architect.resourcePackSwitcher"),
    PLACE_HALF_DOOR       ("architect.place.halfDoor"),
    PLACE_TORCH           ("architect.place.torch"),
    PLACE_DOUBLE_SLAB     ("architect.place.doubleSlab"),
    PLACE_PLANT           ("architect.place.plant"),
    PLACE_PISTON_EXTENSION("architect.place.pistonExtension"),
    PLACE_SIX_SIDED_LOG   ("architect.place.sixSidedLog"),
    BURNING_FURNACE       ("architect.place.burningFurnace"),
    INTERACT_EGG          ("architect.interactDragonEgg"),
    GET_ARMOR             ("architect.get.armor"),
    GET_HEAD              ("architect.get.head"),
    GET_FOOD              ("architect.get.food"),
    GET_LOGS              ("architect.get.logs"),
    GET_DOORS             ("architect.get.doors"),
    GET_SLABS             ("architect.get.slabs"),
    GET_PLANTS            ("architect.get.plants"),
    GET_MISC              ("architect.get.misc"),
    GET_COMMAND           ("architect.getCommand"),
    NO_PHYSICS_LIST       ("architect.noPhysicsList"),
    WE_SCHEMATICS_VIEWER  ("architect.weSchemViewer"),
    VOXEL_VIEWER          ("architect.voxel.viewer"),
    VOXEL_VIEWER_DELETE   ("architect.voxel.delete"),
    STENCIL_LIST_EDITOR   ("architect.voxel.stencilListEditor"),
    BANNER_EDITOR         ("architect.bannerEditor"),
    BANNER_EDITOR_SAVE    ("architect.bannerEditor.save"),
    BANNER_EDITOR_DELETE  ("architect.bannerEditor.delete"),
    PAINTING_EDITOR       ("architect.paintingEditor"),
    ARMOR_STAND_EDITOR    ("architect.armorStandEditor"),
    ARMOR_STAND_EDITOR_TRUSTED ("architect.armorStandEditor.trusted"),
    ARMOR_STAND_EDITOR_DELETE  ("architect.armorStandEditor.delete"),
    ARMOR_STAND_ROLLBACK  ("architect.armorStandRollback"),
    HANGING_ENTITY_EDITOR ("architect.hangingEntityEditor"),
    RANDOMISER_MATERIALS  ("architect.randomiser.allowMaterials"),
    RANDOMISER_USER       ("architect.randomiser.user"),
    CUSTOM_HEAD_USER      ("architect.customHeadManager.user"),
    CUSTOM_HEAD_MANAGER   ("architect.customHeadManager.manager"),
    ARCHITECT_HELP        ("architect.help");

    @Getter
    private final String permissionNode;

    private Permission(String permissionNode) {
        this.permissionNode = permissionNode;
    }

}
