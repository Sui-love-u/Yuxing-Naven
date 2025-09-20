package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;

@Data
@AllArgsConstructor
public class EventMouseClick implements Event {
    private final int key;
    private final boolean state;
}
