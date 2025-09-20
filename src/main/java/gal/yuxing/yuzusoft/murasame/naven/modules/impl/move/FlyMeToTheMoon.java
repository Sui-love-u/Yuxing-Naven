package gal.yuxing.yuzusoft.murasame.naven.modules.impl.move;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventRespawn;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;

@ModuleInfo(name = "FlyMeToTheMoon", description = "Fly me to the moon", category = Category.MOVEMENT)
public class FlyMeToTheMoon extends Module {
    public int ticks = 0;

    @Override
    public boolean onEnable() {
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
        ticks = 0;
        super.onEnable();
        return false;
    }

    @EventTarget
    public void onRespawn(EventRespawn e) {
        toggle();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.PRE && ++ticks > 1) {
            e.setX(e.getX() + 1337);
            e.setZ(e.getZ() + 1337);
        }
    }
}
