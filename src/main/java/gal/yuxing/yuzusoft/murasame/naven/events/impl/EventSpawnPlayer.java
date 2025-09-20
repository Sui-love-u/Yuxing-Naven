package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;
import net.minecraft.client.entity.EntityOtherPlayerMP;

@Data
public class EventSpawnPlayer implements Event {
    private final EntityOtherPlayerMP player;
}
