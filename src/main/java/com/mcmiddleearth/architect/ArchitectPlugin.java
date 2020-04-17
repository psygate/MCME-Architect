/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect;

import com.mcmiddleearth.architect.additionalCommands.AbstractArchitectCommand;
import com.mcmiddleearth.architect.additionalCommands.ArchitectCommand;
import com.mcmiddleearth.architect.chunkUpdate.ChunkUpdateCommand;
import com.mcmiddleearth.architect.additionalCommands.FbtCommand;
import com.mcmiddleearth.architect.additionalCommands.ParrotCommand;
import com.mcmiddleearth.architect.serverResoucePack.RpCommand;
import com.mcmiddleearth.architect.additionalListeners.FbtListener;
import com.mcmiddleearth.architect.additionalListeners.GameMechanicsListener;
import com.mcmiddleearth.architect.additionalListeners.AdditionalProtectionListener;
import com.mcmiddleearth.architect.additionalListeners.OpItemListener;
import com.mcmiddleearth.architect.additionalListeners.StickBlockBreakListener;
import com.mcmiddleearth.architect.specialBlockHandling.listener.BlockCycleListener;
import com.mcmiddleearth.architect.additionalListeners.VoxelBiomeBrushListener;
import com.mcmiddleearth.architect.armorStand.ArmorStandEditorCommand;
import com.mcmiddleearth.architect.armorStand.ArmorStandListener;
import com.mcmiddleearth.architect.bannerEditor.BannerEditorCommand;
import com.mcmiddleearth.architect.bannerEditor.BannerListener;
import com.mcmiddleearth.architect.blockData.BlockDataManager;
import com.mcmiddleearth.architect.chunkUpdate.ChunkUpdateListener;
import com.mcmiddleearth.architect.copyPaste.ClipboardPlayerListener;
import com.mcmiddleearth.architect.copyPaste.CopyCommand;
import com.mcmiddleearth.architect.copyPaste.CutCommand;
import com.mcmiddleearth.architect.copyPaste.FlipCommand;
import com.mcmiddleearth.architect.copyPaste.PasteCommand;
import com.mcmiddleearth.architect.copyPaste.RedoCommand;
import com.mcmiddleearth.architect.copyPaste.RotateCommand;
import com.mcmiddleearth.architect.copyPaste.UndoCommand;
import com.mcmiddleearth.architect.customHeadManager.CustomHeadListener;
import com.mcmiddleearth.architect.customHeadManager.CustomHeadManagerData;
import com.mcmiddleearth.architect.customHeadManager.HeadCommand;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsCommand;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsData;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsListener;
import com.mcmiddleearth.architect.paintingEditor.PaintingListener;
import com.mcmiddleearth.architect.randomiser.RandomiserCommand;
import com.mcmiddleearth.architect.serverResoucePack.RPSwitchTask;
import com.mcmiddleearth.architect.serverResoucePack.RpListener;
import com.mcmiddleearth.architect.serverResoucePack.RpManager;
import com.mcmiddleearth.architect.signEditor.SignCommand;
import com.mcmiddleearth.architect.signEditor.SignListener;
import com.mcmiddleearth.architect.specialBlockHandling.command.GetCommand;
import com.mcmiddleearth.architect.specialBlockHandling.command.InvCommand;
import com.mcmiddleearth.architect.specialBlockHandling.itemBlock.ItemBlockCommand;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialBlockInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.listener.SpecialBlockListener;
import com.mcmiddleearth.architect.specialBlockHandling.data.GetData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialHeadInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialItemInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.data.SpecialSavedInventoryData;
import com.mcmiddleearth.architect.specialBlockHandling.itemBlock.ItemBlockListener;
import com.mcmiddleearth.architect.specialBlockHandling.itemBlock.ItemBlockManager;
import com.mcmiddleearth.architect.specialBlockHandling.listener.BlockPickerListener;
import com.mcmiddleearth.architect.specialBlockHandling.listener.DoorListener;
import com.mcmiddleearth.architect.specialBlockHandling.listener.FurnaceListener;
import com.mcmiddleearth.architect.specialBlockHandling.listener.InventoryListener;
import com.mcmiddleearth.architect.voxelStencilEditor.SlCommand;
import com.mcmiddleearth.architect.voxelStencilEditor.VvCommand;
import com.mcmiddleearth.architect.weSchematicsViewer.SchListCommand;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Eriol_Eandur
 */
public class ArchitectPlugin extends JavaPlugin {
    
    @Getter
    private static ArchitectPlugin pluginInstance;
    
    @Getter
    private final static List<String> commandList = new ArrayList<>();

    private BukkitTask rpSwitchTask;
    
    @Override
    public void onEnable() {
        
        getConfig().options().copyDefaults(true);
        saveConfig();
        pluginInstance = this;
        //ProtocolLibUtil.init(this);
        //DoorListener.addOpenHalfDoorListener();
        PluginData.getMessageUtil().setPluginName("Architect");
        
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ArmorStandListener(), this);
        pluginManager.registerEvents(new BannerListener(), this);
        pluginManager.registerEvents(new PaintingListener(), this);
        pluginManager.registerEvents(new GameMechanicsListener(), this);
        pluginManager.registerEvents(new NoPhysicsListener(), this);
        pluginManager.registerEvents(new FbtListener(), this);
        pluginManager.registerEvents(new SpecialBlockListener(), this);
        pluginManager.registerEvents(new FurnaceListener(), this);
        //pluginManager.registerEvents(new TestListener(), this);
        pluginManager.registerEvents(new VoxelBiomeBrushListener(), this);
        pluginManager.registerEvents(new AdditionalProtectionListener(), this);
        pluginManager.registerEvents(new CustomHeadListener(), this);
        pluginManager.registerEvents(new StickBlockBreakListener(), this);
        pluginManager.registerEvents(new BlockCycleListener(), this);
        pluginManager.registerEvents(new BlockPickerListener(), this);
        pluginManager.registerEvents(new SignListener(), this);
        pluginManager.registerEvents(new DoorListener(), this);
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new RpListener(), this);
        pluginManager.registerEvents(new ChunkUpdateListener(), this);
        pluginManager.registerEvents(new OpItemListener(), this);
        pluginManager.registerEvents(new ClipboardPlayerListener(), this);
        pluginManager.registerEvents(new ItemBlockListener(), this);
//        pluginManager.registerEvents(new AfkListener(), this);
            
        // all CommandExecutors should be subclasses of AbstractArchitectCommand
        // AbstractArchitectCommand methods are used by command /architect help
        setCommandExecutor("armor", new ArmorStandEditorCommand());
        setCommandExecutor("banner", new BannerEditorCommand());
        setCommandExecutor("random", new RandomiserCommand());
        setCommandExecutor("noPhy", new NoPhysicsCommand());
        setCommandExecutor("fbt", new FbtCommand());
        setCommandExecutor("get", new GetCommand());
        setCommandExecutor("sl", new SlCommand());
        setCommandExecutor("vv", new VvCommand());
        setCommandExecutor("schlist",new SchListCommand());
        setCommandExecutor("architect", new ArchitectCommand());
        setCommandExecutor("rp", new RpCommand());
        setCommandExecutor("chead", new HeadCommand());
//        setCommandExecutor("itex", new ItemTexCommand());
        setCommandExecutor("inv", new InvCommand());
        setCommandExecutor("itemblock", new ItemBlockCommand());
        setCommandExecutor("sign", new SignCommand());
        setCommandExecutor("parrot", new ParrotCommand());
        setCommandExecutor("chunkupdate", new ChunkUpdateCommand());
        setCommandExecutor("copy", new CopyCommand());
        setCommandExecutor("cut", new CutCommand());
        setCommandExecutor("paste", new PasteCommand());
        setCommandExecutor("rot", new RotateCommand());
        setCommandExecutor("undo", new UndoCommand());
        setCommandExecutor("redo", new RedoCommand());
        setCommandExecutor("flip", new FlipCommand());
        //setCommandExecutor("speed", new SpeedCommand());
//        setCommandExecutor("newafkk", new NewAfkCommand());
        
        loadData();
        new BlockDataManager().createBlockIdDataMapping();
        
        rpSwitchTask = new RPSwitchTask().runTaskTimer(this, 500, 20);
        ItemBlockManager.startEntityGlowTask();
        
        
        getLogger().info("MCME-Architect Enabled!");
    }
    
    @Override
    public void onDisable() {
        rpSwitchTask.cancel();
        ItemBlockManager.stopEntityGlowTask();
    }
    
    public void setCommandExecutor(String command, AbstractArchitectCommand executor) {
        getCommand(command).setExecutor(executor);
        commandList.add(command);
    }
    
    public void loadData() {
        reloadConfig();
        PluginData.load();
        NoPhysicsData.loadExceptionAreas();
        CustomHeadManagerData.load();
        SpecialBlockInventoryData.loadInventories();
        SpecialSavedInventoryData.loadInventories();
        SpecialItemInventoryData.loadInventories();
        SpecialHeadInventoryData.loadInventory();
        GetData.load();
        RpManager.init();
        ItemBlockManager.init();
    }
    
}
