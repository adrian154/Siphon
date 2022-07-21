package dev.bithole.siphon.core.base.events;

import dev.bithole.siphon.core.SiphonEvent;

import java.net.InetAddress;

public class SiphonPingEvent extends SiphonEvent {

    private final InetAddress address;

    public SiphonPingEvent(InetAddress address) {
        super("ping");
        this.address = address;
    }

}
