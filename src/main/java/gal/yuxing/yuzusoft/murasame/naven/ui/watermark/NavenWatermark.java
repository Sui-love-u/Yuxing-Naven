package gal.yuxing.yuzusoft.murasame.naven.ui.watermark;

import dev.yalan.live.LiveClient;
import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.Version;
import gal.yuxing.yuzusoft.murasame.naven.utils.RenderUtils;
import gal.yuxing.yuzusoft.murasame.naven.utils.StencilUtils;
import gal.yuxing.yuzusoft.murasame.naven.utils.font.FontManager;
import net.minecraft.client.Minecraft;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
@ControlFlowObfuscation
public class NavenWatermark extends WaterMark {
    private final static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private final static String version = Version.getVersion();

    public final static int headerColor = new Color(150, 45, 45, 255).getRGB();
    public final static int bodyColor = new Color(0, 0, 0, 190).getRGB();

    int width;

    public void render() {
        if (Minecraft.getMinecraft().gameSettings.hideGUI || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        Naven naven = Naven.getInstance();

        FontManager fontManager = naven.getFontManager();
        String text = Naven.CLIENT_DISPLAY_NAME + " " + Version.getClientVersion() + " | " + LiveClient.INSTANCE.autoUsername +" | " +   Minecraft.getDebugFPS() /* * 4050 */+ " FPS | " + format.format(new Date());
        width = fontManager.opensans15.getStringWidth(text) + 10;

        StencilUtils.write(false);
        RenderUtils.drawBoundRoundedRect(5, 5, width, 18, 5f, 0xFFFFFFFF);
        StencilUtils.erase(true);

        RenderUtils.drawRectBound(5, 5, width, 3, headerColor);
        RenderUtils.drawRectBound(5, 8, width, 15, bodyColor);

        fontManager.opensans15.drawString(text, 10, 10, 0xFFFFFFFF);
        StencilUtils.dispose();
    }

    public void renderShader() {
        if (Minecraft.getMinecraft().gameSettings.hideGUI || Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            return;
        }

        RenderUtils.drawBoundRoundedRect(5, 5, width, 18, 5f, 0xFFFFFFFF);
    }
}
