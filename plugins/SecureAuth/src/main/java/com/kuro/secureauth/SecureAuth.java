package com.kuro.secureauth;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SecureAuth extends JavaPlugin implements Listener {
    
    private HashMap<UUID, String> authenticatedPlayers = new HashMap<>();
    private HashMap<UUID, Location> frozenLocations = new HashMap<>();
    private HashMap<UUID, Integer> loginAttempts = new HashMap<>();
    private File playerDataFolder;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        playerDataFolder = new File(getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
        
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("register").setExecutor(new AuthCommands(this));
        getCommand("login").setExecutor(new AuthCommands(this));
        getCommand("changepassword").setExecutor(new AuthCommands(this));
        
        getLogger().info("SecureAuth enabled - IP-based authentication active!");
    }
    
    @Override
    public void onDisable() {
        authenticatedPlayers.clear();
        frozenLocations.clear();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerIP = player.getAddress().getAddress().getHostAddress();
        
        frozenLocations.put(player.getUniqueId(), player.getLocation());
        
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        if (!playerFile.exists()) {
            player.sendMessage(ChatColor.YELLOW + "Welcome! Please register with: " + ChatColor.GREEN + "/register <password>");
            return;
        }
        
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
        List<String> trustedIPs = playerData.getStringList("trusted-ips");
        
        if (trustedIPs.contains(playerIP)) {
            authenticatedPlayers.put(player.getUniqueId(), playerIP);
            frozenLocations.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Automatically logged in from trusted IP!");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Please login with: " + ChatColor.GREEN + "/login <password>");
            player.sendMessage(ChatColor.RED + "New IP detected: " + playerIP);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!authenticatedPlayers.containsKey(player.getUniqueId()) && player.isOnline()) {
                        player.kickPlayer(ChatColor.RED + "Login timeout! Please login within 60 seconds.");
                    }
                }
            }.runTaskLater(this, 1200L); // 60 seconds
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        authenticatedPlayers.remove(playerId);
        frozenLocations.remove(playerId);
        loginAttempts.remove(playerId);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isAuthenticated(player)) {
            Location frozen = frozenLocations.get(player.getUniqueId());
            if (frozen != null && (event.getTo().getX() != frozen.getX() || 
                                   event.getTo().getY() != frozen.getY() || 
                                   event.getTo().getZ() != frozen.getZ())) {
                event.setTo(frozen);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isAuthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!isAuthenticated(event.getPlayer())) {
            String command = event.getMessage().toLowerCase().split(" ")[0];
            if (!command.equals("/login") && !command.equals("/register")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "Please login first!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!isAuthenticated(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Please login first!");
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (!isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!isAuthenticated(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (!isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    public boolean isAuthenticated(Player player) {
        return authenticatedPlayers.containsKey(player.getUniqueId());
    }
    
    public void authenticate(Player player) {
        String playerIP = player.getAddress().getAddress().getHostAddress();
        authenticatedPlayers.put(player.getUniqueId(), playerIP);
        frozenLocations.remove(player.getUniqueId());
        loginAttempts.remove(player.getUniqueId());
        
        // Add IP to trusted list
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
        List<String> trustedIPs = playerData.getStringList("trusted-ips");
        if (!trustedIPs.contains(playerIP)) {
            trustedIPs.add(playerIP);
            playerData.set("trusted-ips", trustedIPs);
            try {
                playerData.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void incrementLoginAttempts(Player player) {
        int attempts = loginAttempts.getOrDefault(player.getUniqueId(), 0) + 1;
        loginAttempts.put(player.getUniqueId(), attempts);
        if (attempts >= 3) {
            player.kickPlayer(ChatColor.RED + "Too many failed login attempts!");
        }
    }
    
    public boolean checkPassword(Player player, String password) {
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        if (!playerFile.exists()) return false;
        
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
        String hashedPassword = playerData.getString("password");
        return hashedPassword != null && hashedPassword.equals(hashPassword(password));
    }
    
    public boolean registerPlayer(Player player, String password) {
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        if (playerFile.exists()) return false;
        
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
        playerData.set("username", player.getName());
        playerData.set("password", hashPassword(password));
        playerData.set("trusted-ips", new ArrayList<String>());
        
        try {
            playerData.save(playerFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean changePassword(Player player, String oldPassword, String newPassword) {
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        if (!playerFile.exists()) return false;
        
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
        String hashedPassword = playerData.getString("password");
        
        if (hashedPassword != null && hashedPassword.equals(hashPassword(oldPassword))) {
            playerData.set("password", hashPassword(newPassword));
            try {
                playerData.save(playerFile);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }
    
    public boolean isRegistered(Player player) {
        File playerFile = new File(playerDataFolder, player.getUniqueId().toString() + ".yml");
        return playerFile.exists();
    }
}