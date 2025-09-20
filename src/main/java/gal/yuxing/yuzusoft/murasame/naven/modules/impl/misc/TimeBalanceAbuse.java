package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.*;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.*;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.modules.impl.move.Blink;
import gal.yuxing.yuzusoft.murasame.naven.modules.impl.move.Stuck;
import gal.yuxing.yuzusoft.murasame.naven.ui.clickgui.ClientClickGUI;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.ModeValue;

@ModuleInfo(name = "TimeBalanceAbuse", description = "Abuse time balance", category = Category.MISC)
public class TimeBalanceAbuse extends Module {
    ModeValue mode = ValueBuilder.create(this, "Mode").setDefaultModeIndex(0).setModes("Retain", "Idle", "Release").build().getModeValue();

    @Override
    public void toggle() {
        if (mc.currentScreen instanceof ClientClickGUI) {
            super.toggle();
        }
    }

    @EventTarget
    public void onKey(EventKey e) {
        if (e.getKey() == getKey() && e.isState()) {
            nextState();
        }
    }


    @EventTarget
    public void onMouse(EventMouseClick e) {
        if (e.getKey() == -getKey() && !e.isState()) {
            nextState();
        }
    }

    @EventTarget
    public void onRespawn(EventRespawn e) {
        mode.setCurrentValue(2);
    }

    private void nextState() {
        int index = mode.getCurrentValue() + 1;

        if (index >= mode.getValues().length) {
            index = 0;
        }

        mode.setCurrentValue(index);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.timer = 1;
    }

    @EventTarget
    public void onTick(EventRunTicks e) {
        setSuffix(mode.getCurrentMode());

        if (mc.thePlayer != null) {
            Disabler disabler = (Disabler) Naven.getInstance().getModuleManager().getModule(Disabler.class);
            Blink blink = (Blink) Naven.getInstance().getModuleManager().getModule(Blink.class);

            if (mode.isCurrentMode("Retain") && disabler.delayedServerPackets.size() < 190 && !blink.isEnabled()) {
                mc.thePlayer.timer = 0;
            } else if (mode.isCurrentMode("Idle") || disabler.delayedServerPackets.size() >= 190) {
                mc.thePlayer.timer = 1;
            }
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.PRE && mode.isCurrentMode("Release") && !Naven.getInstance().getModuleManager().getModule(Stuck.class).isEnabled()) {
            Disabler disabler = (Disabler) Naven.getInstance().getModuleManager().getModule(Disabler.class);
             if (disabler.delayedServerPackets.size() > 1) {
                mc.thePlayer.timer = 3;
            } else {
                mc.thePlayer.timer = 1;
            }
        }
    }
}
