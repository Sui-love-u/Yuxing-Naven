package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
import net.minecraft.init.Items;

@ModuleInfo(name = "FastThrow", description = "Throw eggs and snowballs faster", category = Category.MISC)
public class FastThrow extends Module {
    private final FloatValue delay = ValueBuilder.create(this, "Delay").setDefaultFloatValue(0).setFloatStep(1).setMinFloatValue(0).setMaxFloatValue(10).build().getFloatValue();

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.PRE) {
            // check held item is egg or snowball
            if (mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() == Items.egg || mc.thePlayer.getHeldItem().getItem() == Items.snowball || mc.thePlayer.getHeldItem().getItem() == Items.experience_bottle)) {
                if (mc.getRightClickDelayTimer() > delay.getCurrentValue()) {
                    mc.setRightClickDelayTimer((int) delay.getCurrentValue());
                }
            }
        }
    }
}
