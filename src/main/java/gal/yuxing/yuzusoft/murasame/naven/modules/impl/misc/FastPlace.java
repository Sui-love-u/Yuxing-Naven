package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventTick;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.utils.TimeHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "FastPlace", description = "Place blocks faster", category = Category.MISC)
public class FastPlace extends Module {
    TimeHelper timer = new TimeHelper();

    @EventTarget
    public void onTicks(EventTick e) {
        if (Mouse.isButtonDown(mc.gameSettings.keyBindUseItem.getKeyCode() + 100) && timer.delay(1000 / 15f)) {
            if (isHoldingBlock() && mc.currentScreen == null) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);

                timer.reset();
            }
        }
    }

    public boolean isHoldingBlock() {
        return mc.thePlayer != null && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock);
    }
}
