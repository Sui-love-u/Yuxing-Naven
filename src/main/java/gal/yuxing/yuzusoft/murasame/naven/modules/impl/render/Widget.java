package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import com.google.common.collect.Lists;
import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.Priority;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventRender2D;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventShader;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.ui.widgets.*;
import gal.yuxing.yuzusoft.murasame.naven.ui.widgets.*;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

@ModuleInfo(name = "Widget", description = "Widgets", category = Category.RENDER)
public class Widget extends Module {
    BooleanValue items = ValueBuilder.create(this, "Items").setDefaultBooleanValue(true).build().getBooleanValue();
    BooleanValue players = ValueBuilder.create(this, "Players").setDefaultBooleanValue(true).build().getBooleanValue();
    BooleanValue blockRate = ValueBuilder.create(this, "Block Rate").setDefaultBooleanValue(false).build().getBooleanValue();
    BooleanValue targetHud = ValueBuilder.create(this, "Target HUD").setDefaultBooleanValue(false).build().getBooleanValue();
    BooleanValue rearView = ValueBuilder.create(this, "Rear View").setDefaultBooleanValue(false).build().getBooleanValue();

    public List<DraggableWidget> widgets = Lists.newArrayList(new ItemWidget(items), new PlayerWidget(players), new BlockRateWidget(blockRate), new TargetHUDWidget(targetHud), new RearviewWidget(rearView));

    public Widget() {
        super();

        for (DraggableWidget widget : widgets) {
            Naven.getInstance().getEventManager().register(widget);
        }
    }

    @EventTarget(Priority.LOW)
    public void onRender(EventRender2D e) {
        for (DraggableWidget widget : widgets) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(widget.getX().value, widget.getY().value, 0);
            widget.render();
            GlStateManager.popMatrix();
        }
    }

    @EventTarget
    public void onShader(EventShader e) {
        for (DraggableWidget widget : widgets) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(widget.getX().value, widget.getY().value, 0);
            widget.renderShader();
            GlStateManager.popMatrix();
        }
    }

    public void processDrag(int mouseX, int mouseY, int mouseButton) {
        widgets.forEach(widget -> widget.processDrag(mouseX, mouseY, mouseButton));
    }
}
