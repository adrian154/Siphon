# Siphon

Siphon makes building integrations for your Minecraft server easy by exposing a simple, extensible REST API. It is available for both **Forge** and **Spigot**.

For more information on how to use Siphon, check out the [API documentation](docs/API.md) or the [configuration guide](docs/CONFIGURING.md).

# Features

Here's what you can do with Siphon:
* Receive events through webhooks or server-sent events. Siphon comes with a rich set of built-in events:
  * Player chat message
  * Player join
  * Player quit
  * Player death
  * Server list ping
  * Log message
* Define custom HTTP endpoints, or use one of the predefined ones:
  * Run commands
  * Broadcast chat messages
  * Get online players

## Example

Let's say you want to run a task every 24 hours that alerts players and force-saves the world. You could accomplish that using Siphon, the following script, and a cron task.

```shell
USER="backup:your-key-goes-here"
JSON="Content-Type: application/json"

curl --user $USER -X POST localhost:20560/chat -H JSON -d '{"text": "Now backing up the world!"}'
curl --user $USER -X POST localhost:20560/command -H JSON -d '"save-off"'
curl --user $USER -X POST localhost:20560/command -H JSON -d '"save-all flush"'
# ... back up the world ...
curl --user $USER -X POST localhost:20560/command -H JSON -d '"save-on"'
```
