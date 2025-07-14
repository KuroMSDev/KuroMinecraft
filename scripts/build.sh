#!/bin/bash

echo "Building Minecraft Plugin..."

cd /root/minecraft/development/plugins

if ! command -v mvn &> /dev/null; then
    echo "Maven not found. Installing..."
    apt-get update && apt-get install -y maven
fi

mvn clean package

if [ -f target/*.jar ]; then
    echo "Build successful! Copying plugin to server..."
    cp target/*.jar /root/minecraft/minecraft-server/plugins/
    echo "Plugin installed. Restart server to load it."
else
    echo "Build failed!"
fi