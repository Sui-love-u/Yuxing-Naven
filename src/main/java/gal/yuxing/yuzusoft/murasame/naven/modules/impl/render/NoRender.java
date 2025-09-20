package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventRenderEntity;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "NoRender", description = "Disable rendering of certain things.", category = Category.RENDER)
public class NoRender extends Module {
    public BooleanValue arrow = ValueBuilder.create(this, "Arrow").setDefaultBooleanValue(true).build().getBooleanValue();

    @EventTarget
    public void onRender(EventRenderEntity e) {
        if (arrow.getCurrentValue() && e.getEntity() instanceof EntityItem) {
            ItemStack entityItem = ((EntityItem) e.getEntity()).getEntityItem();

            if (entityItem != null && entityItem.getItem() == Items.arrow) {
                e.setCancelled(true);
            }
        }
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.PRE) {
            mc.theWorld.loadedEntityList.forEach(entity -> {
                if (entity instanceof EntityItem) {
                    ItemStack entityItem = ((EntityItem) entity).getEntityItem();

                    if (entityItem != null && entityItem.getItem() == Items.arrow) {
                        mc.theWorld.removeEntity(entity);
                    }
                }
            });
        }
    }
}
