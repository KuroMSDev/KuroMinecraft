# Claude Development Rules

## Git Commit Rules
- **NEVER** add Claude as co-author in commits
- **NEVER** include "Co-Authored-By: Claude" in commit messages
- **NEVER** mention Claude, Anthropic, or AI assistance in commits
- Keep commits clean and professional

## Development Guidelines
- Focus on code functionality
- Use clear, descriptive commit messages
- Maintain consistent code style
- Test changes before committing

## Server Management
- Always run lint/typecheck commands after code changes
- Reload plugins after configuration changes
- Keep backups of important configurations
- **ALWAYS** check and fix firewall rules after server restart:
  ```bash
  iptables -L INPUT -n | grep 25565 || (iptables -A INPUT -p tcp --dport 25565 -j ACCEPT && iptables -A INPUT -p udp --dport 25565 -j ACCEPT)
  ```
- The start.sh script should handle this automatically, but verify if "getsockopt" errors occur