package gal.yuxing.yuzusoft.murasame.naven.ui.cooldown;

import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.utils.RenderUtils;
import gal.yuxing.yuzusoft.murasame.naven.utils.SmoothAnimationTimer;
import gal.yuxing.yuzusoft.murasame.naven.utils.StencilUtils;
import gal.yuxing.yuzusoft.murasame.naven.utils.font.GlyphPageFontRenderer;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.awt.*;
//@jnic.JNICInclude
@ControlFlowObfuscation
@NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
@Data

public class CooldownBar {
    private final static int mainColor = new Color(150, 45, 45, 255).getRGB();
    private final SmoothAnimationTimer animation = new SmoothAnimationTimer(0, 0.2f);
    private final SmoothAnimationTimer yAnimation = new SmoothAnimationTimer(0, 0.2f);
    private final SmoothAnimationTimer startAnimation = new SmoothAnimationTimer(0, 0.3f);
    private int progress = 0;
    private boolean forceExpired = false;
    private String title;
    private long time = Long.MAX_VALUE;
    private long createTime = System.currentTimeMillis();
    private boolean isNewType = true;

    public CooldownBar() {
        this.isNewType = true;
    }

    public CooldownBar(long time, String title) {
        this.time = time;
        this.title = title;
        this.isNewType = false;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getState() {
        if (isNewType && progress >= 0) {
            return (100 - progress) / 100f;
        }
        return (System.currentTimeMillis() - createTime) / (float) time;
    }

    public void render() {
        float state = getState();

        if (state > 0.999f) {
            startAnimation.target = 0;
        } else {
            startAnimation.target = 60;
        }

        startAnimation.update(true);
        animation.target = state;
        animation.update(true);

        StencilUtils.write(false);
        float radius = startAnimation.value;
        if (radius > 5) {
            RenderUtils.circle(50, 9, radius, 0xFFFFFFFF);
        }
        StencilUtils.erase(true);

        GlyphPageFontRenderer font = Naven.getInstance().getFontManager().siyuan16;

        font.drawStringWithShadow(title, 50 - font.getStringWidth(title) / 2f, 0, 0xFFFFFFFF);

        float progressWidth = 100 * (1 - Math.max(0.0f, Math.min(1.0f, animation.value)));

        RenderUtils.drawBoundRoundedRect(0, 13, 100, 5, 2, 0x80000000);

        RenderUtils.drawBoundRoundedRect(0, 13, progressWidth, 5, 2, mainColor);

        StencilUtils.dispose();
    }

    public boolean isExpired() {
        if (forceExpired) return true;
        if (progress >= 100) return true;
        if (!isNewType) {
            return (System.currentTimeMillis() - createTime > time) &&
                    yAnimation.isAnimationDone(true) &&
                    startAnimation.isAnimationDone(true);
        }
        return false;
    }

    public void setProgress(int progress) {
        this.progress = Math.min(100, Math.max(0, progress));
    }

    public void resetProgress() {
        this.progress = 0;
        this.animation.value = 0;
        this.animation.target = 0;
    }

    public void markExpired() {
        this.forceExpired = true;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}