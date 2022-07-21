package dev.bithole.siphon.core;

import dev.bithole.siphon.core.base.events.LogMessageEvent;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name="Siphon", category=Core.CATEGORY_NAME, elementType=Appender.ELEMENT_TYPE)
public class CustomAppender extends AbstractAppender {

    private final SiphonImpl siphon;

    public CustomAppender(SiphonImpl siphon) {
        super(
                "SiphonAppender",
                null,
                PatternLayout.newBuilder().withPattern("%msg").build(),
                false,
                null
        );
        this.siphon = siphon;
    }

    @Override
    public void append(LogEvent event) {
        siphon.broadcastEvent(new LogMessageEvent(event.toImmutable()));
    }

    @Override
    public boolean isStarted() {
        return true;
    }

}
