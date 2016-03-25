/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect;

import com.mcmiddleearth.architect.additionalCommands.ArchitectCommand;
import com.mcmiddleearth.architect.additionalCommands.FbtCommand;
import com.mcmiddleearth.architect.additionalCommands.RpCommand;
import com.mcmiddleearth.architect.additionalListeners.FbtListener;
import com.mcmiddleearth.architect.additionalListeners.GameMechanicsListener;
import com.mcmiddleearth.architect.additionalListeners.HangingEntityProtectionListener;
import com.mcmiddleearth.architect.additionalListeners.VoxelBiomeBrushListener;
import com.mcmiddleearth.architect.armorStand.ArmorStandEditorCommand;
import com.mcmiddleearth.architect.armorStand.ArmorStandListener;
import com.mcmiddleearth.architect.bannerEditor.BannerEditorCommand;
import com.mcmiddleearth.architect.bannerEditor.BannerListener;
import com.mcmiddleearth.architect.customHeadManager.CustomHeadListener;
import com.mcmiddleearth.architect.customHeadManager.CustomHeadManagerData;
import com.mcmiddleearth.architect.customHeadManager.HeadCommand;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsCommand;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsListener;
import com.mcmiddleearth.architect.paintingEditor.PaintingListener;
import com.mcmiddleearth.architect.randomiser.RandomiserCommand;
import com.mcmiddleearth.architect.specialBlockHandling.GetCommand;
import com.mcmiddleearth.architect.specialBlockHandling.SpecialBlockListener;
import com.mcmiddleearth.architect.voxelStencilEditor.SlCommand;
import com.mcmiddleearth.architect.voxelStencilEditor.VvCommand;
import com.mcmiddleearth.architect.weSchematicsViewer.SchListCommand;
import com.mcmiddleearth.util.MessageUtil;
import com.mcmiddleearth.util.ProtocolLibUtil;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class ArchitectPlugin extends JavaPlugin {
    
    @Getter
    private static ArchitectPlugin pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;
        ProtocolLibUtil.init(this);
        MessageUtil.setPREFIX("[Architect] ");
        PluginData.load();
        CustomHeadManagerData.load();
        
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ArmorStandListener(), this);
        pluginManager.registerEvents(new BannerListener(), this);
        pluginManager.registerEvents(new PaintingListener(), this);
        pluginManager.registerEvents(new GameMechanicsListener(), this);
        pluginManager.registerEvents(new NoPhysicsListener(), this);
        pluginManager.registerEvents(new FbtListener(), this);
        pluginManager.registerEvents(new SpecialBlockListener(), this);
        pluginManager.registerEvents(new VoxelBiomeBrushListener(), this);
        pluginManager.registerEvents(new HangingEntityProtectionListener(), this);
        pluginManager.registerEvents(new CustomHeadListener(), this);
            
        getCommand("armor").setExecutor(new ArmorStandEditorCommand());
        getCommand("banner").setExecutor(new BannerEditorCommand());
        getCommand("random").setExecutor(new RandomiserCommand());
        getCommand("noPhy").setExecutor(new NoPhysicsCommand());
        getCommand("fbt").setExecutor(new FbtCommand());
        getCommand("get").setExecutor(new GetCommand());
        getCommand("sl").setExecutor(new SlCommand());
        getCommand("vv").setExecutor(new VvCommand());
        getCommand("schlist").setExecutor(new SchListCommand());
        getCommand("architect").setExecutor(new ArchitectCommand());
        getCommand("rp").setExecutor(new RpCommand());
        getCommand("chead").setExecutor(new HeadCommand());
        
        getLogger().info("MCME-Architect Enabled!");
    }
    
}
