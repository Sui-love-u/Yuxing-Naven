package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;

@Getter
@AllArgsConstructor
public class EventTick implements Event {
    private final EventType type;
}
