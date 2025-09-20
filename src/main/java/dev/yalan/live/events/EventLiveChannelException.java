package dev.yalan.live.events;

import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;

public class EventLiveChannelException implements Event {
    private final Throwable cause;

    public EventLiveChannelException(Throwable cause) {
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
