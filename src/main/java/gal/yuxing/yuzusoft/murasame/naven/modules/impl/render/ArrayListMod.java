package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventRender2D;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventShader;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.ui.arraylist.NavenArrayList;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.ModeValue;

@ModuleInfo(name = "ArrayList", description = "The ArrayList", category = Category.RENDER)
public class ArrayListMod extends Module {
    public BooleanValue prettyModuleName = ValueBuilder.create(this, "Pretty Module Name")
            .setOnUpdate(value -> Module.update = true)
            .setDefaultBooleanValue(false)
            .build()
            .getBooleanValue();

    public BooleanValue hideRenderModules = ValueBuilder.create(this, "Hide Render Modules")
            .setOnUpdate(value -> Module.update = true)
            .setDefaultBooleanValue(false)
            .build()
            .getBooleanValue();

    public BooleanValue rainbow = ValueBuilder.create(this, "Rainbow")
            .setDefaultBooleanValue(true)
            .build()
            .getBooleanValue();

    public FloatValue rainbowSpeed = ValueBuilder.create(this, "Rainbow Speed")
            .setMinFloatValue(1)
            .setMaxFloatValue(20)
            .setDefaultFloatValue(10)
            .setFloatStep(0.1f)
            .build()
            .getFloatValue();

    public FloatValue rainbowOffset = ValueBuilder.create(this, "Rainbow Offset")
            .setMinFloatValue(1)
            .setMaxFloatValue(20)
            .setDefaultFloatValue(10)
            .setFloatStep(0.1f)
            .build()
            .getFloatValue();

    public ModeValue mode = ValueBuilder.create(this, "Mode")
            .setDefaultModeIndex(0)
            .setModes("Right", "Left")
            .build()
            .getModeValue();

    public FloatValue xOffset = ValueBuilder.create(this, "X Offset")
            .setMinFloatValue(-100)
            .setMaxFloatValue(100)
            .setDefaultFloatValue(1)
            .setFloatStep(1)
            .build()
            .getFloatValue();

    public FloatValue yOffset = ValueBuilder.create(this, "Y Offset")
            .setMinFloatValue(1)
            .setMaxFloatValue(100)
            .setDefaultFloatValue(1)
            .setFloatStep(1)
            .build()
            .getFloatValue();

    private final NavenArrayList navenArrayList = new NavenArrayList(this);

    @EventTarget
    public void onRender(EventRender2D e) {
        navenArrayList.draw();
    }

    @EventTarget
    public void onRender(EventShader e) {
        navenArrayList.drawShader();
    }
}
