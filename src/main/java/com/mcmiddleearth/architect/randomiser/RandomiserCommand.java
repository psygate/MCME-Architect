/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.randomiser;

import com.mcmiddleearth.architect.ArchitectPlugin;
import com.mcmiddleearth.architect.Modules;
import com.mcmiddleearth.architect.Permission;
import com.mcmiddleearth.architect.PluginData;
import com.mcmiddleearth.util.CommonMessages;
import com.mcmiddleearth.util.MessageUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RandomiserCommand implements CommandExecutor {

    private final Map<OfflinePlayer, RandomiserConfig> configList = new HashMap<>();
    
    private final static Set<Material> allowedMaterials = new HashSet<>();
    
    private static File configFile;
    
    private static final String configPathAllowedMaterials = "allowed Materials";
    
    public RandomiserCommand() {
        configFile = new File(ArchitectPlugin.getPluginInstance().getDataFolder(), "randomiser.yml");
        if(configFile.exists()) {
            loadAllowedMaterials();
        }
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String c, String[] args) {
        if (!(cs instanceof Player)) {
            CommonMessages.sendPlayerOnlyCommandError(cs);
            return true;
        }
        Player p = (Player) cs;
        if(!PluginData.isModuleEnabled(p.getWorld(), Modules.RANDOMISER)) {
            sendNotActivatedMessage(cs);
            return true;
        } else {
            RandomiserConfig playerConfig =  getPlayerConfig(p);
            if(args.length>0 && (args[0].equalsIgnoreCase("allow") || args[0].equalsIgnoreCase("deny"))){
                if(!PluginData.hasPermission(p, Permission.RANDOMISER_MATERIALS)){
                    CommonMessages.sendNoPermissionError(cs);
                    return true;
                }
                else {
                    if(args.length<2) {
                        sendMissingArgumentErrorMessage(cs);
                        return true;
                    }
                    else {
                        for(int i = 1; i<args.length;i++) {
                            setAllowed(args[i], args[0].equalsIgnoreCase("allow"));
                        }
                        sendMaterialsInfoMessage(cs);
                        saveAllowedMaterials();
                        return true;
                    }
                }
            }
            if (!cs.hasPermission(Permission.RANDOMISER_USER.name())) {
                CommonMessages.sendNoPermissionError(cs);
                return true;
            }
            if(args.length<1) {
                randomise(p.getLocation(), playerConfig);
                MessageUtil.sendInfoMessage(cs, "Randomised your surroundings.");
                return true;
            }
            else if(args[0].equalsIgnoreCase("radius")) {
                if(!checkArgs(cs, args,2)) return true;
                playerConfig.setRadius(getInteger(cs, args[1]));
                sendRadiusSetMessage(cs);
                sendInfoMessage(cs,playerConfig);
                return true;
            }
            else if(args[0].equalsIgnoreCase("range")) {
                if(!checkArgs(cs, args,3)) return true;
                playerConfig.setDataValueRange(getInteger(cs, args[1]),
                                               getInteger(cs, args[2]));
                sendRangeSetMessage(cs);
                sendInfoMessage(cs,playerConfig);
                return true;
            }
            else if(args[0].equalsIgnoreCase("prob")) {
                if(!checkArgs(cs, args,playerConfig.countDataValues()+1)) return true;
                int[] newProbs = new int[playerConfig.countDataValues()];
                for(int i=1; i<args.length;i++) {
                    newProbs[i-1] = getInteger(cs, args[i]);
                }
                playerConfig.setProbs(newProbs);
                sendProbsSetMessage(cs);
                sendInfoMessage(cs,playerConfig);
                return true;
            }
            else if(args[0].equalsIgnoreCase("material")) {
                if(!checkArgs(cs, args,2)) return true;
                String[] materials= new String[args.length-1];
                for(int i=1; i<args.length;i++) {
                    materials[i-1] = args[i];
                }
                if(playerConfig.setMaterials(materials)) {
                    sendMaterialsSetMessage(cs);
                }
                else {
                    sendMaterialsErrorMessage(cs);
                }
                sendInfoMessage(cs,playerConfig);
                return true;
            }
            else if(args[0].equalsIgnoreCase("show")) {
                sendInfoMessage(cs, playerConfig);
                return true;
            }
            else if(args[0].equalsIgnoreCase("showAllowed")) {
                sendMaterialsInfoMessage(cs);
                return true;
            }
            else if(args[0].equalsIgnoreCase("help")) {
                sendHelpMessage(cs);
                return true;
            }
           else {
                CommonMessages.sendInvalidSubcommandError(cs);
                return true;
            }
        }
    }
    
    private RandomiserConfig getPlayerConfig(OfflinePlayer p) {
        for(OfflinePlayer search: configList.keySet()) {
            if(search.getUniqueId().equals(p.getUniqueId())) {
                return configList.get(search);
            }
        }
        RandomiserConfig newConfig = new RandomiserConfig();
        configList.put(p, newConfig);
        return newConfig;
    }
        
    private void randomise(Location center, RandomiserConfig pConfig) {
        int radius = pConfig.getRadius();
        for(int i = center.getBlockX()-radius; i<center.getBlockX()+radius; i++) {
            for(int j = center.getBlockY()-radius; j<center.getBlockY()+radius; j++) {
                for(int k = center.getBlockZ()-radius; k<center.getBlockZ()+radius;k++) {
                    Location loc = new Location(center.getWorld(),i,j,k);
                    if(center.distance(loc)<radius) {
                        Block block = loc.getBlock();
                        if(pConfig.isIn(block.getType())) {
                            block.setData(pConfig.randomDataValue());
                        }
                    }
                }
            }
        }
    }
    
    private int getInteger(CommandSender cs, String str) {
        try {
            return Integer.parseInt(str);
        }
        catch(NumberFormatException e) {
            sendNotANumberErrorMessage(cs);
            return -1;
        }
    }
    
    private boolean checkArgs(CommandSender cs, String[] args, int count) {
        if(args.length<count) {
            sendMissingArgumentErrorMessage(cs);
            return false;
        }
        return true;
    }
    
    public static boolean isAllowed(Material mat) {
        return allowedMaterials.contains(mat);
    }
    
    private void setAllowed(String name, boolean allow) {
        Material material = Material.matchMaterial(name);
        if(material != null) {
            if(allow) {
                allowedMaterials.add(material);
            }
            else {
                allowedMaterials.remove(material);
            }
        }
    }
    
    public void saveAllowedMaterials() {
        FileConfiguration config = new YamlConfiguration();
        List<String> materialNames = new ArrayList<>();
        for(Material mat : allowedMaterials) {
            materialNames.add(mat.name());
        }
        config.set(configPathAllowedMaterials, materialNames);
        try {
            config.save(configFile);
        } catch (IOException ex) {
            Logger.getLogger(ArchitectPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final void loadAllowedMaterials() {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(ArchitectPlugin.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        List<String> materialNames = config.getStringList(configPathAllowedMaterials);
        for(String name : materialNames) {
            Material material= Material.matchMaterial(name);
            if(material!=null) {
                allowedMaterials.add(material);
            } 
            else {
                Logger.getLogger(ArchitectPlugin.class.getName()).log(Level.WARNING,"Material not found.");
            }
        }
    }
    
    private void sendMissingArgumentErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs,"You're missing arguments for this command.");
    }

    private void sendNotANumberErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs,"Not a number.");
   }

    private void sendHelpMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"Tool for randomising data values:");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Randomise your surroundings: /random");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Set affected radius:     /random radius <radius>");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Set placed data values: /random range <min> <max>");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Set affected materials:  /random material <name>,[name]...");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Set probabilities:         /random prob <prob>, [prob]...");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Show configuration:      /random show");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Show allowed materials: /random showAllowed");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Add allowed materials:   /random allow");
        MessageUtil.sendNoPrefixInfoMessage(cs,"- Remove allowed materials: /random deny");
    }
    
    private void sendInfoMessage(CommandSender cs, RandomiserConfig playerConfig) {
        MessageUtil.sendInfoMessage(cs,"randomiser tool configuration:");
        MessageUtil.sendNoPrefixInfoMessage(cs,"Radius: "+playerConfig.getRadius());
        MessageUtil.sendNoPrefixInfoMessage(cs,"Material: "+playerConfig.getMaterials());
        MessageUtil.sendNoPrefixInfoMessage(cs,"DataValueRange: "+playerConfig.getMinValue()+" "
                                         +playerConfig.getMaxValue());
        MessageUtil.sendNoPrefixInfoMessage(cs,"Probabilities: "+playerConfig.getProbs());
    }
    
    private void sendMaterialsInfoMessage(CommandSender cs) {
        String matList = "";
        for(Material material: allowedMaterials) {
            matList = matList+material.name()+" ";
        }
        MessageUtil.sendInfoMessage(cs,"Allowed materials: "+ matList);
    }

    private void sendRadiusSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"Radius set (max is 150).");
    }

    private void sendProbsSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"Probabilities set.");
    }

    private void sendRangeSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"Range of data value set.");
    }

    private void sendMaterialsSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"Materials set.");
    }

    private void sendMaterialsErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs,"Some Materials were not found or are not allowed to be ranomised.");
    }

    private void sendNotActivatedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Field randomizer is not enabled for this world.");
    }

   

}
