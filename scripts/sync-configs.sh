#!/bin/bash

echo "Syncing plugin configurations..."

# Sync EssentialsX configs
if [ -d "/root/minecraft/development/current-plugins/EssentialsX" ]; then
    echo "Syncing EssentialsX..."
    cp -r /root/minecraft/development/current-plugins/EssentialsX/* /root/minecraft/minecraft-server/plugins/Essentials/
fi

# Sync TAB configs
if [ -d "/root/minecraft/development/current-plugins/TAB" ]; then
    echo "Syncing TAB..."
    cp -r /root/minecraft/development/current-plugins/TAB/* /root/minecraft/minecraft-server/plugins/TAB/
fi

echo "Sync complete! Reload plugins in-game with /reload"