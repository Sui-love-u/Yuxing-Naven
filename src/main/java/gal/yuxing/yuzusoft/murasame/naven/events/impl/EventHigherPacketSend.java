package gal.yuxing.yuzusoft.murasame.naven.events.impl;


import gal.yuxing.yuzusoft.murasame.naven.events.api.events.callables.EventCancellable;
import net.minecraft.network.Packet;;

public class EventHigherPacketSend
        extends EventCancellable {
    public Packet packet;

    public EventHigherPacketSend(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

