/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.armorStand.guard;

import com.mcmiddleearth.architect.ArchitectPlugin;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class ArmorStandGuard {

    private final static Map<Player,AbstractRollback> currentRollbacks = new HashMap<>();
    
    private static CleanupTask cleanupTask;

    private static StoreTask storeTask;
    
    private final static int storePeriod = 20*60*10;
    
    static void init() {
        storeTask = new StoreTask();
        storeTask.runTaskTimer(ArchitectPlugin.getPluginInstance(), storePeriod, storePeriod);
    }
    
    static void removeRollback(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void startSingleRollback(Player player, ArmorStand armorStand) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static void startShpereRollback(Player player, Location location, int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static AbstractRollback getRollback(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void setModifiedFlag(ArmorStand armor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static class CleanupTask extends BukkitRunnable {
        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    private static class StoreTask extends BukkitRunnable {
        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    
    
}
