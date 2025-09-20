package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;

@Data
@AllArgsConstructor
public class EventRenderTick implements Event {
    private final EventType type;
}
