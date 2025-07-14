package com.kuro.secureauth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class AuthCommands implements CommandExecutor {
    
    private final SecureAuth plugin;
    
    public AuthCommands(SecureAuth plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (command.getName().equalsIgnoreCase("register")) {
            if (plugin.isRegistered(player)) {
                player.sendMessage(ChatColor.RED + "You are already registered!");
                return true;
            }
            
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /register <password>");
                return true;
            }
            
            String password = args[0];
            if (password.length() < 4) {
                player.sendMessage(ChatColor.RED + "Password must be at least 4 characters long!");
                return true;
            }
            
            if (plugin.registerPlayer(player, password)) {
                plugin.authenticate(player);
                player.sendMessage(ChatColor.GREEN + "Successfully registered and logged in!");
                player.sendMessage(ChatColor.YELLOW + "Your IP has been added to trusted list.");
            } else {
                player.sendMessage(ChatColor.RED + "Registration failed! Please try again.");
            }
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("login")) {
            if (!plugin.isRegistered(player)) {
                player.sendMessage(ChatColor.RED + "You need to register first! Use: /register <password>");
                return true;
            }
            
            if (plugin.isAuthenticated(player)) {
                player.sendMessage(ChatColor.YELLOW + "You are already logged in!");
                return true;
            }
            
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /login <password>");
                return true;
            }
            
            String password = args[0];
            if (plugin.checkPassword(player, password)) {
                plugin.authenticate(player);
                player.sendMessage(ChatColor.GREEN + "Successfully logged in!");
                player.sendMessage(ChatColor.GREEN + "Your IP has been added to trusted list.");
            } else {
                plugin.incrementLoginAttempts(player);
                player.sendMessage(ChatColor.RED + "Incorrect password!");
            }
            return true;
        }
        
        if (command.getName().equalsIgnoreCase("changepassword")) {
            if (!plugin.isAuthenticated(player)) {
                player.sendMessage(ChatColor.RED + "You must be logged in to change your password!");
                return true;
            }
            
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /changepassword <oldpassword> <newpassword>");
                return true;
            }
            
            String oldPassword = args[0];
            String newPassword = args[1];
            
            if (newPassword.length() < 4) {
                player.sendMessage(ChatColor.RED + "New password must be at least 4 characters long!");
                return true;
            }
            
            if (plugin.changePassword(player, oldPassword, newPassword)) {
                player.sendMessage(ChatColor.GREEN + "Password changed successfully!");
            } else {
                player.sendMessage(ChatColor.RED + "Incorrect old password!");
            }
            return true;
        }
        
        return false;
    }
}