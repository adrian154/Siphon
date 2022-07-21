package dev.bithole.siphon.core.base.events;

import dev.bithole.siphon.core.SiphonEvent;
import org.apache.logging.log4j.core.LogEvent;

public class LogMessageEvent extends SiphonEvent {

    private final String threadName;
    private final long timestamp;
    private final String level;
    private final String message;
    private final String loggerName;
    private final String loggerClass;

    public LogMessageEvent(LogEvent event) {
        super("log");
        this.threadName = event.getThreadName();
        this.timestamp = event.getInstant().getEpochMillisecond();
        this.level = event.getLevel().toString();
        this.message = event.getMessage().getFormattedMessage();
        this.loggerName = event.getLoggerName();
        this.loggerClass = event.getLoggerFqcn();
    }

}