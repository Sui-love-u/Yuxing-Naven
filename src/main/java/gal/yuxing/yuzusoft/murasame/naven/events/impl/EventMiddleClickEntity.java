package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;
import net.minecraft.entity.Entity;

@Data
@AllArgsConstructor
public class EventMiddleClickEntity implements Event {
    private final Entity entity;
}
