package com.kuro.skillsxp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class SkillsCommand implements CommandExecutor {
    
    private final SkillsXP plugin;
    
    public SkillsCommand(SkillsXP plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        PlayerSkills skills = plugin.getPlayerSkills(player);
        
        player.sendMessage(ChatColor.GOLD + "=== Your Skills ===");
        
        for (SkillType skill : SkillType.values()) {
            int level = skills.getLevel(skill);
            int xp = skills.getExperience(skill);
            int xpNeeded = skills.getXPForNextLevel(skill);
            
            String progressBar = createProgressBar(xp % 100, 100);
            
            player.sendMessage(ChatColor.YELLOW + skill.getIcon() + " " + skill.getDisplayName() + 
                             ChatColor.WHITE + " - Level " + ChatColor.GREEN + level);
            player.sendMessage(ChatColor.GRAY + "  XP: " + ChatColor.WHITE + xp + 
                             ChatColor.GRAY + " (" + xpNeeded + " to next level)");
            player.sendMessage(ChatColor.GRAY + "  " + progressBar);
        }
        
        return true;
    }
    
    private String createProgressBar(int current, int max) {
        int barLength = 20;
        int filled = (int) ((double) current / max * barLength);
        
        StringBuilder bar = new StringBuilder();
        bar.append(ChatColor.GREEN);
        
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("■");
            } else {
                bar.append(ChatColor.GRAY).append("□");
            }
        }
        
        return bar.toString();
    }
}