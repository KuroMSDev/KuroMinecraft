# Minecraft Plugin Development

## Structure
- `/plugins/` - Your plugin source code
- `/scripts/` - Build and utility scripts
- `/docs/` - Documentation
- `/templates/` - Code templates

## Quick Start

1. Edit plugin code in `/plugins/ExamplePlugin.java`
2. Run build script: `./scripts/build.sh`
3. Restart server to load plugin

## Useful Commands
- Compile: `mvn clean package`
- Install to server: `cp target/*.jar ../minecraft-server/plugins/`
- Reload plugins: `/reload` (in-game)

## Paper API Docs
https://jd.papermc.io/paper/1.21/

## Plugin Ideas
- Custom commands
- Event listeners (block break, player join, etc)
- Custom items/recipes
- Mini-games
- Admin tools