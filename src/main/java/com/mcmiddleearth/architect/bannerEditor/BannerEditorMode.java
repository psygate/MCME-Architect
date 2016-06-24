/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.bannerEditor;

import lombok.Getter;
import org.bukkit.ChatColor;

/**
 *
 * @author Eriol_Eandur
 */
public enum BannerEditorMode {
    LIST    ("l","",": Selects list patterns mode."),
    TEXTURE ("t","",": Selects cycle texture mode."),
    COLOR   ("c","",": Selects cycle color mode."),
    ADD     ("a","",": Selects add pattern mode."),
    REMOVE  ("r","",": Selects remove pattern mode."),
    GET     ("g","",": Selects get banner mode.");
    
    @Getter
    private final String name;

    @Getter
    private final String arguments;
    
    @Getter
    private final String helpText;
    
    private BannerEditorMode(String name, String arguments, String helpText) {
        this.name = name;
        this.helpText = helpText;
        this.arguments = arguments;
    }
    
    public static BannerEditorMode getEditorMode(String name) {
        for(BannerEditorMode type: BannerEditorMode.values()) {
            if(type.name.equalsIgnoreCase(name.substring(0, 1))) {
                return type;
            }
        }
        return null;
    }
    
}
