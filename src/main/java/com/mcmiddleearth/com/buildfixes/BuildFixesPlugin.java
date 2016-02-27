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
package com.mcmiddleearth.com.buildfixes;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author dags_ <dags@dags.me>, Eriol_Eandur
 */

public class BuildFixesPlugin extends JavaPlugin {

    private static Plugin plugin;
    public static boolean multiWorlds;
    public static boolean bfMotd;
    public static HashMap<String, WorldConfig> worlds = new HashMap<String, WorldConfig>();
    public static String motd = "A minecraft server!";
    public static String prim = ChatColor.DARK_AQUA.toString();
    public static String scd = ChatColor.DARK_PURPLE.toString();
    public static String ter = ChatColor.GRAY.toString();

    public BuildFixes() {
        super();
        plugin = this;
    }

    public static Plugin inst() {
        return plugin;
    }

    @Override
    public void onEnable() {
        setupConfig();
        registerListeners();
        registerCommands();
        findWorlds();
        setMotd();
    }

    @Override
    public void onDisable() {
        worlds.clear();
        stencilLists.clear();
    }

    private void setupConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        multiWorlds = this.getConfig().getBoolean("MultiWorldSupport.Enable");
        bfMotd = this.getConfig().getBoolean("Modules.MOTD.Enable");
    }

    private void registerListeners() {
        this.getServer().getPluginManager()
                .registerEvents(new BlockListener(), this);
        this.getServer().getPluginManager()
                .registerEvents(new EnvironmentListener(), this);
        this.getServer().getPluginManager()
                .registerEvents(new BannerEditorListener(), this);
        this.getServer().getPluginManager()
                .registerEvents(new PaintingEditorListener(), this);
        this.getServer().getPluginManager()
                .registerEvents(new ArmorStandEditorListener(), this);
        if (bfMotd) {
            this.getServer().getPluginManager()
                    .registerEvents(new PingListener(), this);
        }
    }

    private void registerCommands() {
        getCommand("get").setExecutor(new Commands());
        getCommand("fbt").setExecutor(new Commands());
        getCommand("vv").setExecutor(new Commands());
        getCommand("schlist").setExecutor(new Commands());
        getCommand("sl").setExecutor(new Commands());
        getCommand("bf").setExecutor(new Commands());
        getCommand("random").setExecutor(new Randomiser(this));
        getCommand("banner").setExecutor(new BannerEditorCommand());
        getCommand("armor").setExecutor(new ArmorStandEditorCommand());
        getCommand("rp").setExecutor(new RpCommand());
    }

    private void findWorlds() {
        getServer().getScheduler().runTask(this, new Runnable() {
            public void run() {
                for (World w : getServer().getWorlds()) {
                    loadWorldSettings(w);
                }
            }
        });
    }

    public void loadWorldSettings(World w) {
        if (multiWorlds) {
            ConfigUtil cfg = new ConfigUtil(this, w.getName());

            cfg.getWorldConfig().options().copyDefaults(true);
            cfg.saveWorldConfig();
        }
        WorldConfig conf = new WorldConfig(w);
        worlds.put(w.getName(), conf);
    }

    private void setMotd() {
        if (bfMotd) {
            String s = this.getConfig().getString("Modules.MOTD.Message");
            motd = s.replace("%", "Â§");
            log("MOTD set!");
        }
    }

    public static void log(String msg) {
        Logger logger = Bukkit.getLogger();
        String pref = "[BuildFixes] ";

        logger.info(pref + msg);
    }
}
