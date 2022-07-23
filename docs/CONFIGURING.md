# Configuring Siphon

All of Siphon's data is persisted in a single file called `siphon.js`, located in the root directory of the server. Here's an example of a Siphon config.

```json
{
  "clients": [
    {
      "name": "test",
      "passwordHash": "CI8QtF4UjWf51IXm21rt046BWbEdc53zgzZw8a0KKk0\u003d",
      "salt": "8ybCIaYPzIPxZkP8hAdTCg\u003d\u003d",
      "permissions": ["*"],
      "webhookURL": "http://localhost/"
    }
  ],
  "port": 20560
}
```

Here is the full structure of the config object:

* `port`: The port which the webserver listens on. Unless you are running the server as a superuser (which you should not be doing), you will have to pick a port above 1024 to bind to.
* `clients`: A list of clients
  * `name`: The client name. This value must be unique.
  * `passwordHash`, `salt`, `keyHash`: Values used for authentication. Don't touch these unless you have a good reason to.
  * `permissions`: A list of [permission nodes](API.md#permissions) associated with the client. Note that Siphon permissions are completely independent of regular Minecraft permissions.
  * `webhookURL`: Optional; if present, events will be delivered to this endpoint via POST request. See the [Webhooks](API.md#webhooks) section of the API documentation.

# Adding a New Client

To add a new client, use the `/addclient` command.

**Usage**

* `/addclient <name> key [permissions...]`
* `/addclient <name> <password> [permissions...]`

**Examples**

* `/addclient awesome-app key players.get chat.broadcast`
* `/addclient admin coolpassword111 *`