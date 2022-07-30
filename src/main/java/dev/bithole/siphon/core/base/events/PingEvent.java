package dev.bithole.siphon.core.base.events;

import dev.bithole.siphon.core.api.SiphonEvent;

import java.net.InetAddress;

public class PingEvent extends SiphonEvent {

    private final InetAddress address;

    public PingEvent(InetAddress address) {
        super("ping");
        this.address = address;
    }

}
