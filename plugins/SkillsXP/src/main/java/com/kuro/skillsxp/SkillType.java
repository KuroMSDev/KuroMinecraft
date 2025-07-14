package com.kuro.skillsxp;

public enum SkillType {
    WOODCUTTING("Woodcutting", "✦"),
    MINING("Mining", "⛏"),
    FARMING("Farming", "☘"),
    COMBAT("Combat", "⚔"),
    FISHING("Fishing", "🎣"),
    BUILDING("Building", "🏗");
    
    private final String displayName;
    private final String icon;
    
    SkillType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getIcon() {
        return icon;
    }
}