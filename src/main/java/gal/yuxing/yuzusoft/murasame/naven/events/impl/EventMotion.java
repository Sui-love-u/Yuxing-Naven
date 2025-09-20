package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.callables.EventCancellable;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;

@Getter
@Setter
@AllArgsConstructor
public class EventMotion extends EventCancellable {
    private final EventType type;
    private double x, y, z;
    private float yaw;
    private float pitch;

    private boolean onGround;

    public EventMotion(EventType type, float yaw, float pitch) {
        this.type = type;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
