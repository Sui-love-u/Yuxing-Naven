package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;

@Data
@AllArgsConstructor
public class EventMoveInput implements Event {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;
}
