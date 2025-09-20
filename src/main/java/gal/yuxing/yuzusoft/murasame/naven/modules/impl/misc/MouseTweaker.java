package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.utils.mousetweaks.MouseTweakerMain;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.ModeValue;

@ModuleInfo(name = "MouseTweaker", description = "Tweaks your mouse!", category = Category.MISC)
public class MouseTweaker extends Module {
    public BooleanValue rmbTweak = ValueBuilder.create(this, "RMB Tweak").setDefaultBooleanValue(true).build().getBooleanValue();
    public BooleanValue lmbTweakWithItem = ValueBuilder.create(this, "LMB Tweak With Item").setDefaultBooleanValue(true).build().getBooleanValue();
    public BooleanValue lmbTweakWithoutItem = ValueBuilder.create(this, "LMB Tweak Without Item").setDefaultBooleanValue(true).build().getBooleanValue();
    public BooleanValue wheelTweak = ValueBuilder.create(this, "Wheel Tweak").setDefaultBooleanValue(true).build().getBooleanValue();
    public ModeValue wheelSearchOrder = ValueBuilder.create(this, "Sort Mode").setDefaultModeIndex(0).setModes("First to last", "Last to first").build().getModeValue();

    public static MouseTweaker getModule() {
        return (MouseTweaker) Naven.getInstance().getModuleManager().getModule(MouseTweaker.class);
    }

    @EventTarget
    public void onRender(EventMotion e) {
        if (e.getType() == EventType.PRE && mc.thePlayer != null) {
            MouseTweakerMain.onUpdateInGame();
        }
    }
}
