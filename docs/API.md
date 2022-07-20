# Siphon API

The Siphon API has three key facets:
* A REST API for querying data or triggering actions on the server
* Server-sent events, allowing clients to stream game events (such as chat, console messages, deaths, etc.)
* Webhooks, for statelessly relaying events to other services

## Design Overview

There are several other projects that do pretty much the exact same thing as Siphon; in my opinion, the two most mature examples are [JSONAPI](https://github.com/alecgorge/jsonapi) and [ServerTap](https://github.com/phybros/servertap). However, these options have some shortcomings:
- JSONAPI is no longer maintained and is not available for modern versions of Minecraft.
- JSONAPI can be accessed over HTTP, but the interface is not RESTful.
- ServerTap lacks a permission management system.
- ServerTap's authentication scheme is rather lacking.
- **Neither plugin is extensible.**

The last point was the primary motivation behind the creation of Siphon. By exposing an API that allows other plugins to register their own events and routes, we allow them to reuse our authentication and event delivery infrastructure. This also obviates the need for multiple plugins to bundle and run their own webservers.

The choice of protocols used by Siphon was made based on the following principles:
- HTTP is spoken by just about everything, and its natural request-response structure make it a perfect fit for querying data and triggering actions.
- Server-sent events are available in virtually every web browser. Since duplex communication is not necessary, we chose not to use WebSocket.
- Webhooks allow events to be delivered without maintaining a stateful connection as demanded by SSE or WebSocket. This also enables interaction with services like IFTTT.