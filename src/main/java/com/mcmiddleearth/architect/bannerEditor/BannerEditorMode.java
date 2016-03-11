/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.bannerEditor;

/**
 *
 * @author Eriol_Eandur
 */
public enum BannerEditorMode {
    LIST    ("l"),
    TEXTURE ("t"),
    COLOR   ("c"),
    ADD     ("a"),
    REMOVE  ("r"),
    GET     ("g");
    
    private final String name;

    private BannerEditorMode(String name) {
        this.name = name;
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
