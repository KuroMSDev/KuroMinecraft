package com.kuro.skillsxp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class SkillsXP extends JavaPlugin {
    
    private HashMap<UUID, PlayerSkills> playerSkills = new HashMap<>();
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new SkillsListener(this), this);
        getCommand("skills").setExecutor(new SkillsCommand(this));
        getLogger().info("SkillsXP enabled!");
    }
    
    @Override
    public void onDisable() {
        saveAllPlayerData();
        getLogger().info("SkillsXP disabled!");
    }
    
    public PlayerSkills getPlayerSkills(Player player) {
        if (!playerSkills.containsKey(player.getUniqueId())) {
            playerSkills.put(player.getUniqueId(), new PlayerSkills(player.getUniqueId()));
        }
        return playerSkills.get(player.getUniqueId());
    }
    
    public void saveAllPlayerData() {
        for (PlayerSkills skills : playerSkills.values()) {
            skills.save(this);
        }
    }
}