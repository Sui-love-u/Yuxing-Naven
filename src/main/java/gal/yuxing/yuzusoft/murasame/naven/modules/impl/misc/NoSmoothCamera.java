package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;

@ModuleInfo(name = "NoSmoothCamera", description = "Disable Smooth Camera!", category = Category.MISC)
public class NoSmoothCamera extends Module {
    public final FloatValue sensitivity = ValueBuilder.create(this, "Sensitivity").setDefaultFloatValue(0.25f).setFloatStep(0.01f).setMinFloatValue(0.1f).setMaxFloatValue(1f).build().getFloatValue();
}
