package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;

@AllArgsConstructor
@Getter
@Setter
public class EventStrafe implements Event {
    private float forward;
    private float strafe;
    private float friction;
    private float yaw;
}
