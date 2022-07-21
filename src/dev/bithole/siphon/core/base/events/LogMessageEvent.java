package dev.bithole.siphon.core.base.events;

import dev.bithole.siphon.core.api.SiphonEvent;
import org.apache.logging.log4j.core.LogEvent;

public class LogMessageEvent extends SiphonEvent {

    private final String thread;
    private final long timestamp;
    private final String level;
    private final String message;
    private final String loggerName;

    public LogMessageEvent(LogEvent event) {
        super("log");
        this.thread = event.getThreadName();
        this.timestamp = event.getInstant().getEpochMillisecond();
        this.level = event.getLevel().toString();
        this.message = event.getMessage().getFormattedMessage();
        this.loggerName = event.getLoggerName();
    }

}
