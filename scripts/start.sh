#!/bin/bash

# Minecraft Server Start Script
# Adjust the -Xmx and -Xms values to allocate more or less RAM

# Ensure firewall rules are in place
echo "Checking firewall rules..."

# Count existing rules for port 25565
TCP_RULES=$(iptables -L INPUT -n | grep -c "tcp dpt:25565" || true)
UDP_RULES=$(iptables -L INPUT -n | grep -c "udp dpt:25565" || true)

# Add TCP rule if missing
if [ "$TCP_RULES" -eq 0 ]; then
    echo "Adding TCP firewall rule for port 25565..."
    iptables -A INPUT -p tcp --dport 25565 -j ACCEPT
else
    echo "TCP firewall rule for port 25565 already exists ($TCP_RULES rules found)"
fi

# Add UDP rule if missing
if [ "$UDP_RULES" -eq 0 ]; then
    echo "Adding UDP firewall rule for port 25565..."
    iptables -A INPUT -p udp --dport 25565 -j ACCEPT
else
    echo "UDP firewall rule for port 25565 already exists ($UDP_RULES rules found)"
fi

# Verify the rules were added
echo "Current firewall rules for port 25565:"
iptables -L INPUT -n | grep 25565 || echo "No rules found - this may cause connection issues!"

echo "Starting Minecraft Server..."
java -Xmx2G -Xms1G -jar paper-1.21.7.jar nogui

# If you want the server to restart automatically on crash, uncomment the lines below:
# while true
# do
#     java -Xmx2G -Xms1G -jar paper-1.21.7.jar nogui
#     echo "Server crashed! Restarting in 5 seconds..."
#     sleep 5
# done