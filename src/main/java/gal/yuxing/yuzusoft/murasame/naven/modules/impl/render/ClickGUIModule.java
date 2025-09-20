package gal.yuxing.yuzusoft.murasame.naven.modules.impl.render;

import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.ui.clickgui.ClientClickGUI;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "ClickGUI", category = Category.RENDER, description = "The ClickGUI")
public class ClickGUIModule extends Module {
    ClientClickGUI clickGUI = null;

    @Override
    protected void initModule() {
        super.initModule();
        setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public boolean onEnable() {
        if (clickGUI == null) {
            clickGUI = new ClientClickGUI();
        }

        super.onEnable();
        mc.displayGuiScreen(clickGUI);
        this.toggle();
        return false;
    }
}
