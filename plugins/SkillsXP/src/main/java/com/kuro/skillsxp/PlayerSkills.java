package com.kuro.skillsxp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerSkills {
    private UUID playerId;
    private HashMap<SkillType, Integer> experience = new HashMap<>();
    private HashMap<SkillType, Integer> levels = new HashMap<>();
    
    public PlayerSkills(UUID playerId) {
        this.playerId = playerId;
        for (SkillType skill : SkillType.values()) {
            experience.put(skill, 0);
            levels.put(skill, 1);
        }
    }
    
    public void addExperience(SkillType skill, int amount) {
        int currentXP = experience.get(skill);
        int newXP = currentXP + amount;
        experience.put(skill, newXP);
        
        int currentLevel = levels.get(skill);
        int newLevel = calculateLevel(newXP);
        if (newLevel > currentLevel) {
            levels.put(skill, newLevel);
        }
    }
    
    public int getExperience(SkillType skill) {
        return experience.get(skill);
    }
    
    public int getLevel(SkillType skill) {
        return levels.get(skill);
    }
    
    private int calculateLevel(int xp) {
        return 1 + (xp / 100);
    }
    
    public int getXPForNextLevel(SkillType skill) {
        int currentLevel = levels.get(skill);
        return (currentLevel * 100) - experience.get(skill);
    }
    
    public void save(SkillsXP plugin) {
        File file = new File(plugin.getDataFolder() + "/players", playerId.toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        for (SkillType skill : SkillType.values()) {
            config.set(skill.name() + ".xp", experience.get(skill));
            config.set(skill.name() + ".level", levels.get(skill));
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void load(SkillsXP plugin) {
        File file = new File(plugin.getDataFolder() + "/players", playerId.toString() + ".yml");
        if (!file.exists()) return;
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (SkillType skill : SkillType.values()) {
            experience.put(skill, config.getInt(skill.name() + ".xp", 0));
            levels.put(skill, config.getInt(skill.name() + ".level", 1));
        }
    }
}