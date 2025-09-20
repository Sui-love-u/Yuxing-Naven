package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.callables.EventCancellable;

@Data
public class EventClick extends EventCancellable {
    private boolean shouldRightClick;
    private int slot;

    public EventClick(final int slot) {
        this.slot = slot;
    }
}
