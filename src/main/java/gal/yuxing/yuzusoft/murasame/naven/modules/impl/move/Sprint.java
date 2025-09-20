package gal.yuxing.yuzusoft.murasame.naven.modules.impl.move;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;

import static gal.yuxing.yuzusoft.murasame.naven.events.api.types.Priority.HIGHEST;

@ModuleInfo(name = "Sprint", description = "Automatically sprints", category = Category.MOVEMENT)
public class Sprint extends Module {
    @EventTarget(HIGHEST)
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.PRE) {
            if ((mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0)
                    && mc.thePlayer.getFoodStats().getFoodLevel() > 6 && !mc.thePlayer.isSneaking()) {
                mc.gameSettings.keyBindSprint.pressed = true;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
    }
}
