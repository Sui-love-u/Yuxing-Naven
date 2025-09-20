package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;

@ModuleInfo(name = "FullBright", description = "Make your world brighter.", category = Category.RENDER)
public class FullBright extends Module {
    public FloatValue brightness = ValueBuilder.create(this, "Brightness").setDefaultFloatValue(1).setFloatStep(0.1f).setMinFloatValue(0f).setMaxFloatValue(1f).build().getFloatValue();
}
