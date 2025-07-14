package com.kuro.skillsxp;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class SkillsListener implements Listener {
    
    private final SkillsXP plugin;
    
    public SkillsListener(SkillsXP plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PlayerSkills skills = plugin.getPlayerSkills(player);
        
        if (isWood(block.getType())) {
            skills.addExperience(SkillType.WOODCUTTING, 1);
            sendActionBar(player, SkillType.WOODCUTTING, 1);
        }
        else if (isOre(block.getType())) {
            int xp = getOreXP(block.getType());
            skills.addExperience(SkillType.MINING, xp);
            sendActionBar(player, SkillType.MINING, xp);
        }
        else if (isCrop(block.getType())) {
            skills.addExperience(SkillType.FARMING, 1);
            sendActionBar(player, SkillType.FARMING, 1);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerSkills skills = plugin.getPlayerSkills(player);
        
        skills.addExperience(SkillType.BUILDING, 1);
        sendActionBar(player, SkillType.BUILDING, 1);
    }
    
    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            PlayerSkills skills = plugin.getPlayerSkills(player);
            
            int xp = 5;
            skills.addExperience(SkillType.COMBAT, xp);
            sendActionBar(player, SkillType.COMBAT, xp);
        }
    }
    
    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getCaught() != null) {
            Player player = event.getPlayer();
            PlayerSkills skills = plugin.getPlayerSkills(player);
            
            skills.addExperience(SkillType.FISHING, 10);
            sendActionBar(player, SkillType.FISHING, 10);
        }
    }
    
    private void sendActionBar(Player player, SkillType skill, int xpGained) {
        PlayerSkills skills = plugin.getPlayerSkills(player);
        String message = ChatColor.GREEN + "+" + xpGained + " " + skill.getIcon() + " " + skill.getDisplayName() + " XP " +
                        ChatColor.GRAY + "[" + skills.getExperience(skill) + "/" + (skills.getLevel(skill) * 100) + "]";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
    
    private boolean isWood(Material material) {
        return material.name().contains("LOG") || material.name().contains("WOOD");
    }
    
    private boolean isOre(Material material) {
        return material.name().contains("ORE") || 
               material == Material.ANCIENT_DEBRIS;
    }
    
    private boolean isCrop(Material material) {
        return material == Material.WHEAT || 
               material == Material.CARROTS || 
               material == Material.POTATOES || 
               material == Material.BEETROOTS ||
               material == Material.SUGAR_CANE ||
               material == Material.MELON ||
               material == Material.PUMPKIN;
    }
    
    private int getOreXP(Material material) {
        switch (material) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                return 1;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                return 2;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                return 3;
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                return 5;
            case ANCIENT_DEBRIS:
                return 10;
            default:
                return 1;
        }
    }
}