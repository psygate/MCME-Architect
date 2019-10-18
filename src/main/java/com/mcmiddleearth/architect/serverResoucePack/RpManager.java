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
package com.mcmiddleearth.architect.serverResoucePack;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.serverResoucePack.RegionEditConversation.RegionEditConversationFactory;
import com.mcmiddleearth.util.DevUtil;
import com.mcmiddleearth.util.DynmapUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class RpManager {
    
    private static final File playerFile = new File(ArchitectPlugin.getPluginInstance().getDataFolder(),"/playerRpData.dat");
    private static final File regionFolder = new File(ArchitectPlugin.getPluginInstance().getDataFolder(),"regions");
    
    @Getter
    private static Map<String, RpRegion> regions = new HashMap<>();
    
    private static Map<UUID,RpPlayerData> playerRpData = new HashMap<>();
    
    @Getter
    private static RegionEditConversationFactory regionEditConversationFactory
            = new RegionEditConversationFactory(ArchitectPlugin.getPluginInstance());
    
    public static void init() {
        loadPlayerData();
        if(!regionFolder.exists()) {
            regionFolder.mkdir();
        }
        regions.clear();
        for(File file: regionFolder.listFiles((File dir, String name) -> name.endsWith(".reg"))) {
            try {
                YamlConfiguration config = new YamlConfiguration();
                config.load(file);
                final ConfigurationSection section = config.getConfigurationSection("rpRegion");
                new BukkitRunnable() {
                    int counter = 10;
                    @Override
                    public void run() {
                        RpRegion region = RpRegion.loadFromMap((Map<String,Object>)section.getValues(true));
                        if(region!=null) {
                            DevUtil.log("loaded region: "+region.getName());
                            addRegion(region);  
                            cancel();
                        } else {
                            counter--;
                            DevUtil.log("failed to load region: "+region+" tries left: "+counter);
                            if(counter<1) {
                                cancel();
                            }
                        }
                    }
                }.runTaskTimer(ArchitectPlugin.getPluginInstance(), 200, 20);
            } catch (IOException | InvalidConfigurationException ex) {
                Logger.getLogger(RpManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static RpRegion getRegion(Location loc) {
        RpRegion maxWeight = null;
        for(RpRegion region: regions.values()) {
            if((maxWeight==null || region.getWeight() > maxWeight.getWeight()) 
                    && region.contains(loc)) {
                maxWeight = region;
            }
        }
        return maxWeight;
    }
    
    public static RpRegion getRegion(String name) {
        return regions.get(name);
    }
    
    public static void updateDynmapRegions() {
        DynmapUtil.clearMarkers();
        regions.values().forEach((region) -> {
            DynmapUtil.createMarker(region);
        });
    }
    
    public static boolean removeRegion(String name) {
        RpRegion region = regions.get(name);
        if(region!=null) {
            regions.remove(name);
            new File(regionFolder,name+".reg").delete();
            updateDynmapRegions();
            return true;
        }
        return false;
    }
    
    public static void addRegion(RpRegion region) {
        regions.put(region.getName(), region);
        updateDynmapRegions();
    }
    
    public static RpPlayerData getPlayerData(Player player) {
        RpPlayerData data = playerRpData.get(player.getUniqueId());
        if(data == null) {
            data = new RpPlayerData();
            playerRpData.put(player.getUniqueId(), data);
        }
        return data;
    }
    
    /**
     * Gets the first configured URL of the given rp name.
     * @param rp name of the RP
     * @param player player to use rp setting from, may be null for default settings
     * @return first confiured URL in config.yml
     */
    public static String getRpUrl(String rp, Player player) {
        ConfigurationSection section = getConfigSection(rp, player);
        if(section!=null) {
            return section.getString("url");
        }
        return "";
    }
    
    public static byte[] getSHA(String rp, Player player) {
        byte[] result = new byte[20];
        ConfigurationSection section = getConfigSection(rp, player);
        if(section!=null) {
            String sha = section.getString("sha");
            /*for(int i=0; i<sha.length();i+=2) {
                result[i/2] = Byte.parseByte(sha.substring(i, i+2),16);
            }*/
            return stringToSHA(sha);
        }
        return result;
    }
    
    private static byte[] stringToSHA(String sha) {
        byte[] result = new byte[20];
        if(sha==null) {
            return result;
        }
        result = new BigInteger(sha.trim(),16).toByteArray();
        if(result.length>20) {
            result = Arrays.copyOfRange(result,1,21);
        }
//Logger.getGlobal().info("First sha byte: " +result[0]+ " "+result[19]);
        return result;
    }
    
    private static ConfigurationSection getConfigSection(String rp, Player player) {
        RpPlayerData data;
        if(player!=null) {
            data = getPlayerData(player);
        } else {
            data = new RpPlayerData();
        }
        ConfigurationSection config = getRpConfig().getConfigurationSection(rp);
        if(config != null) {
            ConfigurationSection section = config.getConfigurationSection(getResolutionKey(data.getResolution()));
            if(section==null) {
                section = config.getConfigurationSection(config.getKeys(false).iterator().next());
            }
            ConfigurationSection varSection = section.getConfigurationSection(data.getVariant());
            String url;
            if(varSection==null) {
                return section.getConfigurationSection(section.getKeys(false).iterator().next());
            } else {
                return varSection;
            }
        }
        return null;
    }
        
    public static String getCurrentRpName(Player player) {
        /*RpRegion region = getPlayerData(player).getCurrentRegion();
        if(region!=null) {
            return region.getRp();
        }
        return "";*/
        return getRpForUrl(getPlayerData(player).getCurrentRpUrl());
    }

    public static String matchRpName(String rpKey) {
        for(String search: getRpConfig().getKeys(false)) {
            if(search.toLowerCase().startsWith(rpKey.toLowerCase())) {
                return search;
            }
        }
        return "";
    }
    
    public static boolean setRp(String rpName, Player player) {
        String url = getRpUrl(rpName, player);
        RpPlayerData data = getPlayerData(player);
        if(!url.equals("") && !url.equals(data.getCurrentRpUrl())) {
            data.setCurrentRpUrl(url);
Logger.getGlobal().info("set Resouce Pack for: "+player.getName()+" "+rpName);
            player.setResourcePack(url, getSHA(rpName, player));
            savePlayerData();
            return true;
        }
        return false;
    }
    
    public static byte[] getSHAForUrl(String url) {
        for(String rpName: getRpConfig().getKeys(false)) {
            ConfigurationSection section = getRpConfig().getConfigurationSection(rpName);
            for(String key: section.getKeys(false)) {
                ConfigurationSection pxSection = section.getConfigurationSection(key);
                for(String varKey: pxSection.getKeys(false)) {
                    ConfigurationSection varSection = pxSection.getConfigurationSection(varKey);
                    if(varSection.getString("url").equals(url)) {
                        return stringToSHA(varSection.getString("sha"));
                    }
                }
            }
        }
        return new byte[20];
    }
    
    public static String getRpForUrl(String url) {
        for(String rpName: getRpConfig().getKeys(false)) {
            ConfigurationSection section = getRpConfig().getConfigurationSection(rpName);
            for(String key: section.getKeys(false)) {
                ConfigurationSection pxSection = section.getConfigurationSection(key);
                for(String varKey: pxSection.getKeys(false)) {
                    ConfigurationSection varSection = pxSection.getConfigurationSection(varKey);
                    if(varSection.getString("url").equals(url)) {
                        return rpName;
                    }
                }
            }
        }
        return "";
    }
    
    public static boolean searchRpKey(String key) {
        return getRpConfig().getKeys(true).stream().anyMatch((search) -> (search.endsWith(key)));
    }
    
    private static ConfigurationSection getRpConfig() {
        return ArchitectPlugin.getPluginInstance().getConfig().getConfigurationSection("ServerResourcePacks");
    }

    public static boolean refreshSHA(CommandSender cs, String rp) {
Logger.getGlobal().info("rp: "+rp);
        ConfigurationSection config = getRpConfig().getConfigurationSection(rp);
        if(config!=null) {
            for(String resolutionKey: config.getKeys(false)) {
Logger.getGlobal().info("resolutionKey: "+resolutionKey);
                ConfigurationSection resolutionSection = config.getConfigurationSection(resolutionKey);
                for(String variantKey: resolutionSection.getKeys(false)) {
                    try {
Logger.getGlobal().info("varianKey: "+variantKey);
                        ConfigurationSection variantSection = resolutionSection.getConfigurationSection(variantKey);
Logger.getGlobal().info(variantSection.getString("url"));
                        URL url = new URL(variantSection.getString("url"));
                        InputStream fis = url.openStream();
                        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
                        //FileInputStream fis = new FileInputStream(file);
                        
                        byte[] data = new byte[1024];
                        int read = 0;
                        long time = System.currentTimeMillis();
                        while ((read = fis.read(data)) != -1) {
                            sha1.update(data, 0, read);
                            if(System.currentTimeMillis()-time>5000) {
                                time = System.currentTimeMillis();
                                PluginData.getMessageUtil().sendInfoMessage(cs, "calculating ...");
                            }
                        }
                        byte[] hashBytes = sha1.digest();
                        StringBuilder sb = new StringBuilder();
                        for (byte b : hashBytes) {
                            sb.append(String.format("%02x", b));
                        }
                        String hashString = sb.toString();
                        //String hashString = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(hashBytes)).toString();
                        variantSection.set("sha", hashString);
                        ArchitectPlugin.getPluginInstance().saveConfig();
                    } catch (IOException | NoSuchAlgorithmException ex) {
                        Logger.getLogger(RpManager.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    public static String getResolutionKey(int px) {
        return px+"px";
    }
    
    public static void savePlayerData() {
        try {
            if(!playerFile.exists()) {
                playerFile.createNewFile();
            }
            try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(playerFile))) {
                DevUtil.log("Saving player RP data");
                out.writeObject(playerRpData);
            }
        } catch (IOException ex) {
            Logger.getLogger(RpManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loadPlayerData() {
        if(playerFile.exists()) {
            try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(playerFile))) {
                playerRpData = (Map<UUID,RpPlayerData>) in.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(RpManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void saveRpRegion(RpRegion region) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("rpRegion", region.saveToMap());
            config.save(new File(regionFolder,region.getName()+".reg"));
        } catch (IOException ex) {
            Logger.getLogger(RpManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
