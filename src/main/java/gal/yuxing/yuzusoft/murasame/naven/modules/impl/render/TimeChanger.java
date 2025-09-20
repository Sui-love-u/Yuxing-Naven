package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventPacket;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleInfo(name = "TimeChanger", description = "Change the time of the world", category = Category.RENDER)
public class TimeChanger extends Module {
    FloatValue time = ValueBuilder.create(this, "World Time").setDefaultFloatValue(8000).setFloatStep(1).setMinFloatValue(0).setMaxFloatValue(24000).build().getFloatValue();

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.PRE) {
            mc.theWorld.setWorldTime((long) (time.getCurrentValue()));
        }
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }
}
