package com.kuro.hpbars;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HPBars extends JavaPlugin implements Listener {
    
    private Map<UUID, ArmorStand> healthBars = new HashMap<>();
    private boolean showForPassive;
    private boolean showForHostile;
    private double displayRange;
    private String healthFormat;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();
        
        getServer().getPluginManager().registerEvents(this, this);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAllHealthBars();
            }
        }.runTaskTimer(this, 0L, 1L);
        
        getLogger().info("HPBars has been enabled!");
    }
    
    @Override
    public void onDisable() {
        for (ArmorStand armorStand : healthBars.values()) {
            armorStand.remove();
        }
        healthBars.clear();
        getLogger().info("HPBars has been disabled!");
    }
    
    private void loadConfiguration() {
        showForPassive = getConfig().getBoolean("show-for-passive", true);
        showForHostile = getConfig().getBoolean("show-for-hostile", true);
        displayRange = getConfig().getDouble("display-range", 10.0);
        healthFormat = getConfig().getString("health-format", "&c❤ &f{current}/{max}");
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player) && !(event.getEntity() instanceof ArmorStand)) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (shouldShowHealthBar(entity)) {
                createHealthBar(entity);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player)) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (!healthBars.containsKey(entity.getUniqueId()) && shouldShowHealthBar(entity)) {
                createHealthBar(entity);
            }
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        UUID entityId = event.getEntity().getUniqueId();
        if (healthBars.containsKey(entityId)) {
            ArmorStand armorStand = healthBars.get(entityId);
            armorStand.remove();
            healthBars.remove(entityId);
        }
    }
    
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (org.bukkit.entity.Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity && healthBars.containsKey(entity.getUniqueId())) {
                ArmorStand armorStand = healthBars.get(entity.getUniqueId());
                armorStand.remove();
                healthBars.remove(entity.getUniqueId());
            }
        }
    }
    
    private boolean shouldShowHealthBar(LivingEntity entity) {
        EntityType type = entity.getType();
        
        if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || type == EntityType.SPIDER || 
            type == EntityType.CREEPER || type == EntityType.ENDERMAN || type == EntityType.WITCH ||
            type == EntityType.PHANTOM || type == EntityType.BLAZE || type == EntityType.GHAST ||
            type == EntityType.MAGMA_CUBE || type == EntityType.SLIME || type == EntityType.WITHER_SKELETON ||
            type == EntityType.PILLAGER || type == EntityType.VINDICATOR || type == EntityType.EVOKER ||
            type == EntityType.VEX || type == EntityType.RAVAGER || type == EntityType.GUARDIAN ||
            type == EntityType.ELDER_GUARDIAN || type == EntityType.SHULKER || type == EntityType.ENDERMITE ||
            type == EntityType.SILVERFISH || type == EntityType.CAVE_SPIDER || type == EntityType.PIGLIN ||
            type == EntityType.PIGLIN_BRUTE || type == EntityType.HOGLIN || type == EntityType.ZOGLIN ||
            type == EntityType.STRIDER || type == EntityType.WARDEN || type == EntityType.ZOMBIE_VILLAGER ||
            type == EntityType.HUSK || type == EntityType.STRAY || type == EntityType.DROWNED) {
            return showForHostile;
        } else if (type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP ||
                   type == EntityType.CHICKEN || type == EntityType.HORSE || type == EntityType.DONKEY ||
                   type == EntityType.MULE || type == EntityType.LLAMA || type == EntityType.PARROT ||
                   type == EntityType.RABBIT || type == EntityType.WOLF || type == EntityType.CAT ||
                   type == EntityType.OCELOT || type == EntityType.FOX || type == EntityType.PANDA ||
                   type == EntityType.BEE || type == EntityType.TURTLE || type == EntityType.DOLPHIN ||
                   type == EntityType.COD || type == EntityType.SALMON || type == EntityType.PUFFERFISH ||
                   type == EntityType.TROPICAL_FISH || type == EntityType.SQUID || type == EntityType.GLOW_SQUID ||
                   type == EntityType.AXOLOTL || type == EntityType.GOAT || type == EntityType.FROG ||
                   type == EntityType.TADPOLE || type == EntityType.ALLAY || type == EntityType.SNIFFER ||
                   type == EntityType.CAMEL || type == EntityType.VILLAGER || type == EntityType.WANDERING_TRADER) {
            return showForPassive;
        }
        
        return true;
    }
    
    private void createHealthBar(LivingEntity entity) {
        Location loc = entity.getLocation().add(0, entity.getHeight() + 0.3, 0);
        ArmorStand armorStand = (ArmorStand) entity.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setMarker(true);
        
        updateHealthBar(entity, armorStand);
        
        healthBars.put(entity.getUniqueId(), armorStand);
    }
    
    private void updateHealthBar(LivingEntity entity, ArmorStand armorStand) {
        double health = entity.getHealth();
        double maxHealth = entity.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
        
        String display = healthFormat
            .replace("{current}", String.format("%.1f", health))
            .replace("{max}", String.format("%.1f", maxHealth));
        
        armorStand.setCustomName(ChatColor.translateAlternateColorCodes('&', display));
    }
    
    private void updateAllHealthBars() {
        healthBars.entrySet().removeIf(entry -> {
            LivingEntity entity = (LivingEntity) getServer().getEntity(entry.getKey());
            ArmorStand armorStand = entry.getValue();
            
            if (entity == null || entity.isDead() || !entity.isValid()) {
                armorStand.remove();
                return true;
            }
            
            boolean hasNearbyPlayer = entity.getWorld().getPlayers().stream()
                .anyMatch(player -> player.getLocation().distance(entity.getLocation()) <= displayRange);
            
            if (!hasNearbyPlayer) {
                armorStand.setCustomNameVisible(false);
            } else {
                armorStand.setCustomNameVisible(true);
                Location newLoc = entity.getLocation().add(0, entity.getHeight() + 0.3, 0);
                armorStand.teleport(newLoc);
                updateHealthBar(entity, armorStand);
            }
            
            return false;
        });
    }
}