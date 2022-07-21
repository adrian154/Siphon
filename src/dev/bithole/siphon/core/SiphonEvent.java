package dev.bithole.siphon.core;

public abstract class SiphonEvent {

    public final String event;

    public SiphonEvent(String name) {
        this.event = name;
    }

}