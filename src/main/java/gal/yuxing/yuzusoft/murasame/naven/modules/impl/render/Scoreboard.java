package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;

@ModuleInfo(name = "Scoreboard", description = "Renders scoreboard", category = Category.RENDER)
public class Scoreboard extends Module {
    public BooleanValue modernStyle = ValueBuilder.create(this, "Modern Style").setDefaultBooleanValue(true).build().getBooleanValue();
    public BooleanValue noRenderPoint = ValueBuilder.create(this, "No Render Points").setDefaultBooleanValue(true).build().getBooleanValue();
    public FloatValue down = ValueBuilder.create(this, "Down").setDefaultFloatValue(0).setFloatStep(1).setMinFloatValue(0).setMaxFloatValue(500).build().getFloatValue();
}
