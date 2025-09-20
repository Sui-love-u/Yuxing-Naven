package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.callables.EventCancellable;

@Getter
@AllArgsConstructor
public class EventClientChat extends EventCancellable {
    private final String message;
}
