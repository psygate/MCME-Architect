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
package com.mcmiddleearth.architect.customHeadManager;

import com.google.common.io.BaseEncoding;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mcmiddleearth.architect.ArchitectPlugin;
import static com.mcmiddleearth.util.ConfigurationUtil.deserializeLocation;
import static com.mcmiddleearth.util.ConfigurationUtil.serializeLocation;
import com.mcmiddleearth.util.FileUtil;
import com.mcmiddleearth.util.HeadUtil;
import com.mcmiddleearth.util.MessageUtil;
import com.mojang.util.UUIDTypeAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class CustomHeadManagerData {
    
    @Getter
    private static final File acceptedHeadDir = new File(ArchitectPlugin.getPluginInstance()
                                                       .getDataFolder(),"customHeads/accepted");
    
    @Getter
    private static final File submittedHeadDir = new File(ArchitectPlugin.getPluginInstance()
                                                       .getDataFolder(),"customHeads/submitted");
    
    @Getter
    private static final String fileExtension = "yml";
    
    @Getter
    private static CustomHeadCollection collection = new CustomHeadCollection();
    
    @Getter
    @Setter
    private static CustomHeadGallery gallery;
    
    private static final String CONFIG_KEY = "headGalleryLocation";
    
    static {
        if(!acceptedHeadDir.exists()) {
            acceptedHeadDir.mkdirs();
        }
        if(!submittedHeadDir.exists()) {
            submittedHeadDir.mkdirs();
        }
    }
    
    public static void load() {
        if(gallery!=null) {
            gallery.remove();
        }
        collection = new CustomHeadCollection();
        List<File> headFiles = getFiles(acceptedHeadDir);
        for(File headFile : headFiles) {
            String headName = headFile.toString().substring(0,headFile.toString().lastIndexOf("."));
            headName = headName.replace('\\', '/');
            headName = headName.substring(acceptedHeadDir.toString().length()+1);
            collection.addHead(headName, CustomHeadData.fromFile(headFile));
        }
        if(!ArchitectPlugin.getPluginInstance().getConfig().isConfigurationSection(CONFIG_KEY)) {
            return;
        }
        Map<String,Object> galleryLoc = ArchitectPlugin.getPluginInstance()
                                            .getConfig()
                                            .getConfigurationSection(CONFIG_KEY)
                                            .getValues(true);
        if(galleryLoc == null){
            return;
        }
        Location loc = deserializeLocation(galleryLoc);
        if(loc == null) {
            return;
        }
        gallery = new CustomHeadGallery(collection, loc);
    }
    
    private static List<File> getFiles(File directory) {
        File[] subDirs = directory.listFiles(FileUtil.getDirFilter());
        List<File> fileList = new ArrayList<>();
        File[] headFiles = directory.listFiles(FileUtil.getFileExtFilter(fileExtension));
        Collections.addAll(fileList, headFiles);
        for(File subDir : subDirs) {
            fileList.addAll(getFiles(subDir));
        }
        return fileList;
    }
    
    public static void saveGallery() {
        ConfigurationSection config = ArchitectPlugin.getPluginInstance().getConfig();
        config.set(CONFIG_KEY, serializeLocation(gallery.getLocation()));
        ArchitectPlugin.getPluginInstance().saveConfig();
    }
    
    public static ItemStack getSumittedHead(String name) {
        File file = new File(submittedHeadDir, name+"."+fileExtension);
        if(!file.exists()) {
            return null;
        }
        CustomHeadData data = CustomHeadData.fromFile(file);
        if(data==null) {
            return null;
        } else {
            return HeadUtil.getCustomHead(name, data.getHeadId(), data.getTexture());
        }
    }
    
    public static ItemStack getHead(String name) {
        File file = new File(acceptedHeadDir, name+"."+fileExtension);
        if(!file.exists()) {
            return null;
        }
        CustomHeadData data = CustomHeadData.fromFile(file);
        if(data==null) {
            return null;
        } else {
            return HeadUtil.getCustomHead(name, data.getHeadId(), data.getTexture());
        }
    }
    
    public static String getHeadName(UUID headId) {
        return collection.getHeadName(headId);
    }
    
    public static boolean acceptHead(String name, String newName) {
        CustomHeadData data = getSubmittedHeadData(name);
        if(data!=null) {
            File file = new File(acceptedHeadDir, newName+"."+fileExtension);
            if(file.exists()) {
                return false;
            }
            if(data.saveToFile(file)){
                if(gallery!=null) {
                    gallery.remove();
                }
                collection.addHead(newName, data);
                file = new File(submittedHeadDir, name+"."+fileExtension);
                file.delete();
                if(gallery!=null) {
                    gallery.place();
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean rejectHead(String name) {
        File file = new File(submittedHeadDir, name+"."+fileExtension);
        if(file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
    
    public static boolean deleteHead(String name) {
        File file = new File(acceptedHeadDir, name+"."+fileExtension);
        if(file.exists()) {
            if(gallery!=null) {
                gallery.remove();
            }
            collection.removeHead(name);
            if(gallery!=null) {
                gallery.place();
            }
            file.delete();
            return true;
        }
        return false;
    }
    
    public static boolean renameHead(String oldName, String newName) {
        File oldFile = new File(acceptedHeadDir, oldName+"."+fileExtension);
        File newFile = new File(acceptedHeadDir, newName+"."+fileExtension);
        if(oldFile.exists() && !newFile.exists()) {
            CustomHeadData data = getHeadData(oldName);
            if(data.saveToFile(newFile)) {
                if(gallery!=null) {
                    gallery.remove();
                }
                collection.removeHead(oldName);
                collection.addHead(newName, data);
                oldFile.delete();
                if(gallery!=null) {
                    gallery.place();
                }
                return true;
            }
        }
        return false;
    }
    
    public static CustomHeadData getSubmittedHeadData(String name) {
        File file = new File(submittedHeadDir, name+"."+fileExtension);
        if(!file.exists()) {
            return null;
        }
        return CustomHeadData.fromFile(file);
    }
    
    public static CustomHeadData getHeadData(String name) {
        File file = new File(acceptedHeadDir, name+"."+fileExtension);
        if(!file.exists()) {
            return null;
        }
        return CustomHeadData.fromFile(file);
    }
    
    public static void submitHead(Player owner, String name) {
        new HeadDataBuilder(owner,name);
    }
    
    private static class HeadDataBuilder {
        
        private boolean received;
        
        private HttpURLConnection connection;

        private String mojangUrl = "https://sessionserver.mojang.com/session/minecraft/profile/%s";
        
        public HeadDataBuilder(final Player owner, final String name) {
            received = false; 
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(received) {
                        try {
                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                String json = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json);
                                JsonArray jProperties = jsonObject.getAsJsonArray("properties");
                                String textures="";
                                for(JsonElement jProperty : jProperties) {
                                    if(jProperty.getAsJsonObject().has("name")
                                            && jProperty.getAsJsonObject().get("name").getAsString().equals("textures")) {
                                        textures = jProperty.getAsJsonObject().get("value").getAsString();
                                        break;
                                    }
                                }
                                jsonObject = new JsonParser().parse(new String(BaseEncoding.base64().decode(textures)))
                                                             .getAsJsonObject();
                                jsonObject = jsonObject.getAsJsonObject("textures");
                                jsonObject = jsonObject.getAsJsonObject("SKIN");
                                String url = jsonObject.get("url").getAsString();
                                url = BaseEncoding.base64().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
                                UUID headId = new UUID(owner.getUniqueId().getMostSignificantBits()+System.currentTimeMillis(),
                                                       owner.getUniqueId().getLeastSignificantBits()+System.currentTimeMillis());
                                CustomHeadData headData = new CustomHeadData(headId, owner.getUniqueId(),url);
                                //ToDo check for heads with same texture
                                File file = new File(submittedHeadDir,name+"."+fileExtension);
                                int index = 0;
                                while(file.exists()) {
                                    index++;
                                    file = new File(submittedHeadDir,name+index+"."+fileExtension);
                                }
                                if(headData.saveToFile(file)) {
                                    MessageUtil.sendInfoMessage(owner,"Your head has been submitted.");
                                    cancel();
                                    return;
                                }
                            } else {
                                MessageUtil.sendErrorMessage(owner, "Error. Mojang server didn't respond. Wait one minute at last before you try again.");
                                cancel();
                                return;
                                }
                        } catch (IOException | JsonSyntaxException ex) {
                            Logger.getLogger(CustomHeadManagerData.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            cancel();
                        }
                        MessageUtil.sendErrorMessage(owner, "Error. Your head has not been submitted.");
                    }
                }
            }.runTaskTimer(ArchitectPlugin.getPluginInstance(), 10, 10);
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(String.format(mojangUrl, UUIDTypeAdapter.fromUUID(owner.getUniqueId())));
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setReadTimeout(5000);
                    } catch (IOException ex) {
                        Logger.getLogger(CustomHeadManagerData.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        received = true;
                    }
                }
            }.runTaskAsynchronously(ArchitectPlugin.getPluginInstance());
        }
    }
}
