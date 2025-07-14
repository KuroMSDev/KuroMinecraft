package com.kuro.anonymity;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.UUID;

public class TrueAnonymity extends JavaPlugin implements Listener {
    
    private HashMap<UUID, String> playerAliases = new HashMap<>();
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("TrueAnonymity enabled - hiding real names!");
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String realName = player.getName();
        String alias = getConfig().getString("aliases." + realName, realName);
        
        if (!realName.equals(alias)) {
            playerAliases.put(player.getUniqueId(), alias);
            event.setJoinMessage(ChatColor.YELLOW + alias + " joined the game");
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String alias = playerAliases.getOrDefault(player.getUniqueId(), player.getName());
        
        if (!player.getName().equals(alias)) {
            event.setQuitMessage(ChatColor.YELLOW + alias + " left the game");
        }
        
        playerAliases.remove(player.getUniqueId());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String alias = playerAliases.getOrDefault(player.getUniqueId(), player.getName());
        
        if (!player.getName().equals(alias)) {
            String deathMessage = event.getDeathMessage();
            if (deathMessage != null) {
                event.setDeathMessage(deathMessage.replace(player.getName(), alias));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String alias = playerAliases.getOrDefault(player.getUniqueId(), player.getName());
        
        if (!player.getName().equals(alias)) {
            event.setCancelled(true);
            
            String format = event.getFormat();
            String message = String.format(format, alias, event.getMessage());
            
            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(message);
            }
        }
    }
}