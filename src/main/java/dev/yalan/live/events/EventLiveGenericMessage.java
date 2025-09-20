package dev.yalan.live.events;

import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;

public class EventLiveGenericMessage implements Event {
    private final String channel;
    private final String message;

    public EventLiveGenericMessage(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }
}
