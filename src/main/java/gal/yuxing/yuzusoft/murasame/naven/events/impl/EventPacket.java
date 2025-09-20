package gal.yuxing.yuzusoft.murasame.naven.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.callables.EventCancellable;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import net.minecraft.network.Packet;

@Getter @Setter
@AllArgsConstructor
public class EventPacket extends EventCancellable {
    private final EventType type;
    private Packet<?> packet;

    private final EventState eventType;
    public enum EventState {
        SEND,
        RECEIVE
    }
}
