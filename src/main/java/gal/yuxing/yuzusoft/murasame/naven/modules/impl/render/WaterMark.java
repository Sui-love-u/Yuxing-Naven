package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventRender2D;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventShader;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.ui.watermark.NavenWatermark;
import gal.yuxing.yuzusoft.murasame.naven.ui.watermark.SilenceFixWaterMark;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.ModeValue;

@ModuleInfo(name = "WaterMark", description = "The WaterMark", category = Category.RENDER)
public class WaterMark extends Module {
    private gal.yuxing.yuzusoft.murasame.naven.ui.watermark.WaterMark waterMark;

    public ModeValue mode = ValueBuilder.create(this, "Mode").setDefaultModeIndex(0).setModes("Naven", "SilenceFix").setOnUpdate(value -> {
        if (value.getModeValue().isCurrentMode("Naven")) {
            waterMark = new NavenWatermark();
        } else {
            waterMark = new SilenceFixWaterMark();
        }
    }).build().getModeValue();

    @EventTarget
    public void onRender(EventRender2D e) {
        if (waterMark != null) {
            waterMark.render();
        }
    }

    @EventTarget
    public void onRender(EventShader e) {
        if (waterMark != null) {
            waterMark.renderShader();
        }
    }
}
