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
package com.mcmiddleearth.architect.armorStand;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

/**
 *
 * @author Eriol_Eandur
 */
public class ArmorStandUtil {
    
    public static Map<String,Object> serializeEntity(Entity entity) {
        Map<String,Object> result = new HashMap<>();
        Location loc = entity.getLocation();
        result.put("location", serializeLocation(loc));
        return result;
    }
    
    public static Map<String,Object> serializeArmorStand(ArmorStand armor) {
        Map<String,Object> result = serializeEntity(armor);

        Map<String,Object> items = new HashMap<>();
        items.put("boots", armor.getBoots().serialize());
        items.put("chestplate", armor.getChestplate().serialize());
        items.put("helmet", armor.getHelmet().serialize());
        items.put("leggins", armor.getLeggings().serialize());
        items.put("hand", armor.getItemInHand().serialize());
        
        Map<String,Object> pose = new HashMap<>();
        pose.put("body", serializeEulerAngle(armor.getBodyPose()));
        pose.put("leftArm", serializeEulerAngle(armor.getLeftArmPose()));
        pose.put("rightArm", serializeEulerAngle(armor.getRightArmPose()));
        pose.put("leftLeg", serializeEulerAngle(armor.getLeftLegPose()));
        pose.put("rightLeg", serializeEulerAngle(armor.getRightLegPose()));
        pose.put("head", serializeEulerAngle(armor.getHeadPose()));
        pose.put("hand", serializeEulerAngle(armor.getBodyPose()));
        
        result.put("items", items);
        result.put("pose", pose);
        result.put("arms", armor.hasArms());
        result.put("base", armor.hasBasePlate());
        result.put("gravity", armor.hasGravity());
        //result.put("marker", armor.isMarker());
        result.put("small", armor.isSmall());
        result.put("visible", armor.isVisible());
        return result;
    }
    
    public static ArmorStand deserializeArmorStand(Map<String,Object> data) {
        Location loc = deserializeLocation((Map<String,Object>)data.get("location"));
        if(loc == null) {
            return null;
        }
        else {
            ArmorStand armor = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            if(armor != null) {
                Map<String,Object> pose = getMap(data,"pose"); //(Map<String,Object>)data.get("pose");
                if(pose!=null) {
                    armor.setBodyPose(deserializeEulerAngle(getMap(pose,"body")));//(Map<String,Object>)pose.get("body")));
                    armor.setLeftLegPose(deserializeEulerAngle(getMap(pose,"leftLeg")));//(Map<String,Object>)pose.get("leftLeg")));
                    armor.setRightLegPose(deserializeEulerAngle(getMap(pose,"rightLeg")));//(Map<String,Object>)pose.get("rightLeg")));
                    armor.setLeftArmPose(deserializeEulerAngle(getMap(pose,"leftArm")));//(Map<String,Object>)pose.get("leftArm")));
                    armor.setRightArmPose(deserializeEulerAngle(getMap(pose,"rightArm")));//(Map<String,Object>)pose.get("rightArm")));
                    armor.setHeadPose(deserializeEulerAngle(getMap(pose,"head")));//(Map<String,Object>)pose.get("head")));
                }

                Map<String,Object> items = getMap(data,"items");//(Map<String,Object>) data.get("items");
                if(items!=null) {
                    armor.setBoots(ItemStack.deserialize(getMap(items,"boots")));//(Map<String,Object>)items.get("boots")));
                    armor.setLeggings(ItemStack.deserialize(getMap(items,"leggins")));//(Map<String,Object>)items.get("leggins")));
                    armor.setChestplate(ItemStack.deserialize(getMap(items,"chestplate")));//(Map<String,Object>)items.get("chestplate")));
                    armor.setHelmet(ItemStack.deserialize(getMap(items,"helmet")));//(Map<String,Object>)items.get("helmet")));
                    armor.setItemInHand(ItemStack.deserialize(getMap(items,"hand")));//(Map<String,Object>)items.get("hand")));
                }
                
                try {
                    armor.setArms((Boolean)data.get("arms"));
                    armor.setBasePlate((Boolean)data.get("base"));
                    armor.setVisible((Boolean)data.get("visible"));
                    //armor.setMarker((boolean)armor.get("marker"));
                    armor.setSmall((Boolean)data.get("small"));
                    armor.setGravity((Boolean)data.get("gravity"));
                }
                catch(NullPointerException e) {}
            }
            return armor;
        }
    }
    
    public static Map<String,Object> serializeLocation(Location loc) {
        Map<String,Object> result = new HashMap<>();
        result.put("x", loc.getX());
        result.put("y", loc.getY());
        result.put("z", loc.getZ());
        result.put("yaw", loc.getYaw());
        result.put("pitch", loc.getPitch());
        result.put("world", loc.getWorld().getName());
        return result;
    }
    
    public static Location deserializeLocation(Map<String,Object> data) {
        World world = Bukkit.getWorld((String) data.get("world"));
        if(world == null) {
            return null;
        }
        else {
            return new Location(world, (Double) data.get("x"), 
                                       (Double) data.get("y"), 
                                       (Double) data.get("z"), 
                                       (Float) data.get("yaw"),
                                       (Float) data.get("pitch"));
        }
    }
    
    private static Map<String,Object> serializeEulerAngle(EulerAngle angle) {
        Map<String,Object> result = new HashMap<>();
        result.put("x", angle.getX());
        result.put("y", angle.getY());
        result.put("z", angle.getZ());
        return result;
    }
    
    private static EulerAngle deserializeEulerAngle(Map<String,Object> data) {
        return new EulerAngle((Double) data.get("x"),
                              (Double) data.get("y"),
                              (Double) data.get("z"));
    }
    
    private static Map<String,Object> getMap(Map<String,Object> data, String key) {
        Object value = data.get(key);
        if(value instanceof ConfigurationSection) {
            return ((ConfigurationSection)value).getValues(true);
        }
        else {
            return (Map<String,Object>) value;
        }
    }
}
