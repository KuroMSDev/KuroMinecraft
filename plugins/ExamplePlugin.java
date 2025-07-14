package com.example.myplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ExamplePlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getLogger().info("ExamplePlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("ExamplePlugin has been disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hello")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.GREEN + "Hello, " + player.getName() + "!");
            } else {
                sender.sendMessage("Hello, Console!");
            }
            return true;
        }
        return false;
    }
}