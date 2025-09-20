package gal.yuxing.yuzusoft.murasame.naven.modules.impl.move;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventRunTicks;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(name = "InventoryMove", description = "Allows you to move while your inventory is open", category = Category.MOVEMENT)
public class InventoryMove extends Module {
    @EventTarget
    public void onMotion(EventRunTicks e) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
            mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
            mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint) || Naven.getInstance().getModuleManager().getModule(Sprint.class).isEnabled();
        }
    }
}
