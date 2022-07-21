# Siphon API

The Siphon API has three key facets:
* A REST API for querying data or triggering actions on the server
* Server-sent events, allowing clients to stream game events (such as chat, console messages, deaths, etc.)
* Webhooks, for statelessly relaying events to other services

## Design Goals

There are several other projects that do pretty much the exact same thing as Siphon; in my opinion, the two most mature examples are [JSONAPI](https://github.com/alecgorge/jsonapi) and [ServerTap](https://github.com/phybros/servertap). However, these options have some shortcomings:
- JSONAPI is no longer maintained and is not available for modern versions of Minecraft.
- JSONAPI can be accessed over HTTP, but the interface is not RESTful.
- ServerTap lacks a permission management system.
- ServerTap's authentication scheme is rather lacking.
- **Neither plugin is extensible.**

The last point was the primary motivation behind the creation of Siphon. By exposing an API that allows other plugins to register their own events and routes, we allow them to reuse our authentication and event delivery infrastructure. This also obviates the need for multiple plugins to bundle and run their own webservers.

The choice of protocols used by Siphon was made based on the following principles:
- HTTP is spoken by just about everything, and its natural request-response structure make it a perfect fit for querying data and triggering actions.
- Server-sent events are available in virtually every web browser. We chose to use SSE over WebSocket for a variety of reasons:
    - Duplex communication is not necessary, so SSE is sufficient for our use-case
    - SSE clients in browsers feature auto-reconnection  
    - SSE is easier to reverse-proxy
- Webhooks allow events to be delivered without maintaining a stateful connection as demanded by SSE or WebSocket. This also enables interaction with services like IFTTT.

## Package Layout

Siphon is designed with portability in mind, since ports to Fabric/Forge are planned. Thus, efforts have been made to decouple the core functionality of Siphon from the loader-specific parts. As a rule, all classes in `dev.bithole.siphon.core` should be portable between plugin/mod environments. Implementations for the basic endpoints can be found in the `dev.bithole.siphon.base` package.

# Error Reporting

If a request could not be completed, the response will have the appropriate 4xx or 5xx error code. The body of the response will be a JSON object with a field named `error` containing a human-readable message describing the error.

# Authentication

Clients may be configured to authenticate with a user-provided password, or with a randomly generated 256-bit secret. The former authentication method should only be used with human users who will be directly accessing the API through a web interface.

Credentials are sent along with every request in the form of an `Authorization` header. The HTTP Basic authentication must be used, where the username is the client ID and the password is the password/API secret.

Here is some example JavaScript code to construct the Authorization header:

```js
header = `Basic ${Buffer.from(`${clientID}:${secret}`).toString("base64")}`;
```

If the credentials for a request were missing or could not be determined, a 400 status code is sent in response. If the credentials are incorrect, a 401 status code is sent.

If a request failed to authenticate (i.e. a response with a status of 401 was received), the client must not send any other requests for 1000ms. Any requests sent during this period will be denied with a status code of 429.

# Permissions

# Endpoints

# Events

Events are a method of notifying all authorized clients that something has occurred. They can be received through webhooks or server-sent events.

Whether a client is authorized to receive an event is determined using the permissions system. For example, only clients with the `event.log` permission node will receive the `log` event.

Event names *should* be prefixed with the implementing plugin's name to avoid conflicts.

## Webhooks

A webhook is essentially an HTTP-based callback. The client specifies a URL which they would like to receive events on, and when an event occurs, a POST request is made to that URL.

At the moment, Siphon does not support any mechanism for the recipient to verify that a request actually originated from Siphon. To mitigate spoofing, webhook URLs should be made difficult to guess (e.g. by adding a large random identifier).

All requests to webhook URLs will be made with the `User-Agent` header set to `Siphon`.

## Server-Sent Events

Events can be streamed using the [Server Sent Events](https://html.spec.whatwg.org/multipage/server-sent-events.html) API from the `/events` endpoint.

# Built-In Events

Several useful events are built into Siphon.

## Event: log

This event fires whenever a message is logged.

```
{
  "event": "log",
  "message": String,
  "loggerName": String,
  "level": String,
  "timestamp": Number,
  "thread": String
}
```

## Event: chat

This event fires whenever a player sends a chat message.

```
{
  "event": "chat",
  "message": String,
  "uuid": String,
  "playerName": String
}
```

## Event: player-death

This event fires whenever a player dies.

```
{
  "event": "player-death",
  "uuid": String,
  "playerName": String,
  "message": String
}
```

## Event: player-quit

This event fires whenever a player quits.

```
{
  "event": "player-quit",
  "uuid": String,
  "playerName": String
}
```

## Event: ping

This event fires whenever the server receives a server list ping.

```
{
  "event": "ping",
  "address": String
}
```

## Event: enable

This event fires whenever the Siphon plugin is enabled, usually when the server starts.

```
{
  "event": "enable"
}
```

## Event: disable

This event fires whenever the Siphon plugin is disabled, usually when the server stops. Note that if the server crashes this event may not be sent.

```
{
  "event": "disable"
}
```