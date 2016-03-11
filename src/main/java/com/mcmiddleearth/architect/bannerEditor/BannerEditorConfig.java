/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.bannerEditor;

import com.mcmiddleearth.architect.ArchitectPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class BannerEditorConfig {
    
    @Getter
    private static final File dataDir = new File(ArchitectPlugin.getPluginInstance().getDataFolder()+"/banners");
    
    @Getter
    private static final String fileExtension = "yml";
    
    private int patternId = 0;
    
    private BannerEditorMode editorMode = BannerEditorMode.LIST;

    public BannerEditorMode getEditorMode() {
        return editorMode;
    }

    public void setEditorMode(BannerEditorMode editorMode) {
        this.editorMode = editorMode;
    }

    public int getPatternId() {
        return patternId;
    }

    public void setPatternId(int patternId) {
        this.patternId = patternId;
    }
    
    public boolean saveBanner(ItemStack banner, String fileName, String description) throws IOException {
        YamlConfiguration data = new YamlConfiguration();
        data.set("description", description);
        data.set("Banner", banner.serialize());
        File saveFile = new File(dataDir+"/"+fileName+"."+fileExtension);
        if(!saveFile.exists()) {
            data.save(saveFile);
            return true;
        }
        else {
            return false;
        }
    }
    
    public ItemStack loadBanner(String filename){
        File file = new File(dataDir+"/"+filename+"."+fileExtension);
        YamlConfiguration data = new YamlConfiguration();
        try {
            data.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            return null;
        }
        Map<String, Object> bannerData = data.getConfigurationSection("Banner").getValues(true);
        if(bannerData==null) {
            return null;
        }
        return ItemStack.deserialize(bannerData);
    }
}
