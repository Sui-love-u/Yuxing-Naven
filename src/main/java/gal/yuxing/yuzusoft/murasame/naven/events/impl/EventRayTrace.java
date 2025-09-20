package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;
import net.minecraft.util.Vec3;

@AllArgsConstructor
@Data
public class EventRayTrace implements Event {
    private Vec3 positionEyes, look;
    private double blockReachDistance;
    private final float partialTicks;
}
