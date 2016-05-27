
package com.mcmiddleearth.architect.armorStand;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.architect.armorStand.guard.ArmorStandGuard;
import static com.mcmiddleearth.pluginutil.ConfigurationUtil.deserializeLocation;
import static com.mcmiddleearth.pluginutil.ConfigurationUtil.serializeLocation;
import com.mcmiddleearth.pluginutil.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class ArmorStandEditorConfig {
    
    private ArmorStandPart part = ArmorStandPart.HEAD;
    
    private ArmorStandEditorMode editorMode = ArmorStandEditorMode.COPY;
    
    private int rotationStep = 10;
    
    private MemoryConfiguration copiedEntity;
    
    @Getter
    private static final File dataDir = new File(ArchitectPlugin.getPluginInstance().getDataFolder(),"armorStands");
    
    @Getter
    private static final String fileExtension = "yml";
    
    public ArmorStandEditorConfig(Player p) {
        if(!dataDir.exists()) {
            dataDir.mkdirs();
        }
        clearCopiedArmorStand();
        if(!PluginData.hasPermission(p, Permission.ARMOR_STAND_EDITOR)) {
            editorMode = ArmorStandEditorMode.ROLLBACK;
        }
     }

    public void placeArmorStand(Location loc, boolean exact) {
        Map<String,Object> data = (Map<String,Object>)copiedEntity.get("ArmorStand");
        Location saved = deserializeLocation((Map<String,Object>) data.get("location"));
        if(!exact) {
            loc.setX(loc.getBlockX()+saved.getX()-saved.getBlockX());
            loc.setZ(loc.getBlockZ()+saved.getZ()-saved.getBlockZ());
            loc.setYaw(saved.getYaw());
        }
        data.put("location",serializeLocation(loc));
        ArmorStand armor = ArmorStandUtil.deserializeArmorStand((Map<String,Object>)copiedEntity.get("ArmorStand"));
        ArmorStandGuard.setModifiedFlag(armor);
        data.put("location",serializeLocation(saved));
    }
    
    public final void clearCopiedArmorStand() {
        copiedEntity = new MemoryConfiguration();
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("location", serializeLocation(new Location(Bukkit.getWorlds().get(0),0.5,0,0.5,0,0)));
        copiedEntity.set("ArmorStand", data);
    }
    
    public void copyArmorStand(ArmorStand armor) {
        copiedEntity = new MemoryConfiguration();
        copiedEntity.set("ArmorStand",ArmorStandUtil.serializeArmorStand(armor));
    }
    
    public boolean saveArmorStand(String filename, String description) throws IOException {
        YamlConfiguration data = new YamlConfiguration();
        data.set("description", description);
        data.set("ArmorStand", copiedEntity.get("ArmorStand"));
        File saveFile = new File(dataDir, filename+"."+fileExtension);
        if(!saveFile.exists()) {
            data.save(saveFile);
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean loadArmorStand(String filename) throws IOException, InvalidConfigurationException {
        File file = new File(dataDir,filename+"."+fileExtension);
        if(file.exists()) {
            YamlConfiguration data = new YamlConfiguration();
            data.load(file);
            copiedEntity = new MemoryConfiguration();
            Map<String, Object> armorData = data.getConfigurationSection("ArmorStand").getValues(true);
            armorData.put("location", serializeLocation(new Location(Bukkit.getWorlds().get(0),0.5,0,0.5,0,0)));
            copiedEntity.set("ArmorStand",armorData);
            return true;
        }
        else {
            return false;
        }
    }
    
    public File[] getFiles(String folder) {
        File dir;
        if(folder == null || folder.equals("")) {
            dir = dataDir;
        }
        else {
            dir = new File(dataDir,folder);
        }
        List<File> list = new ArrayList<File>();
        File[] files = dir.listFiles(FileUtil.getDirFilter());
        Set<File> set = new TreeSet<File>();
        if(files.length>0) {
            set.addAll(Arrays.asList(files));
            list.addAll(set);
            set.clear();
        }
        files = dir.listFiles(FileUtil.getFileExtFilter("yml"));
        if(files.length>0) {
            set.addAll(Arrays.asList(files));
            list.addAll(set);
        }
        return list.toArray(new File[0]);
    }
    
    public boolean deleteFile(String filename) {
        boolean result = false;
        File file = new File(dataDir, filename+".yml");
        if(file.exists()) {
            file.delete();
            result = true;
        }
        else {
            result =  false;
        }
        file = new File(dataDir, filename);
        if(file.exists() && file.isDirectory()) {
            if(file.listFiles().length==0) {
                file.delete();
                result = true;
            }
            else {
                result = false;
            }
        }
        return result;
    }
    
    public String getDescription(File file) {
        if(file.exists()) {
            YamlConfiguration data = new YamlConfiguration();
            try {
                data.load(file);
            } catch (IOException ex) {
                return "";
            } catch (InvalidConfigurationException ex) {
                return "";
            }
            return data.getString("description");
        }
        else {
            return "";
        }
    }
    
    public ArmorStandEditorMode getEditorMode() {
        return editorMode;
    }

    public void setEditorMode(ArmorStandEditorMode editorMode) {
        this.editorMode = editorMode;
    }

    public ArmorStandPart getPart() {
        return part;
    }

    public void setPart(ArmorStandPart part) {
        this.part = part;
    }
    
    public void setRotationStep(int stepInDegree) {
        rotationStep = stepInDegree;
    }
    
    public int getRotationStep() {
        return rotationStep;
    }
    
}
