package dev.bithole.siphon.core;

public abstract class SiphonEvent {

    public final String name;

    public SiphonEvent(String name) {
        this.name = name;
    }

}