/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.architect.armorStand;

/**
 *
 * @author Eriol_Eandur
 */
public enum ArmorStandEditorMode {
    HAND        ("h"),
    XMOVE       ("mx"),
    YMOVE       ("my"),
    ZMOVE       ("mz"),
    MOVE        ("mo"),
    XROTATE     ("x"),
    YROTATE     ("y"),
    ZROTATE     ("z"),
    ROTATE      ("r"),
    TURN        ("t"),
    BASE        ("b"),
    MARKER      ("ma"),
    ARMS        ("a"),
    SIZE        ("s"),
    VISIBLE     ("v"),
    GRAVITY     ("g"),
    PASTE       ("p"),
    COPY        ("c");
    
    private final String name;

    private ArmorStandEditorMode(String name) {
        this.name = name;
    }
    
    public static ArmorStandEditorMode getEditorMode(String name) {
        for(ArmorStandEditorMode type: ArmorStandEditorMode.values()) {
            if(name.toLowerCase().startsWith(type.name)) {
                return type;
            }
        }
        return null;
    }
    
}
