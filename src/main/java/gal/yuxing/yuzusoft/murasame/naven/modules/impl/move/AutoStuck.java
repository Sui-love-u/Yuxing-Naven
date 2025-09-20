package gal.yuxing.yuzusoft.murasame.naven.modules.impl.move;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventPacket;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc.AutoClip;
import gal.yuxing.yuzusoft.murasame.naven.utils.MoveUtils;
import gal.yuxing.yuzusoft.murasame.naven.utils.TimeHelper;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleInfo(name = "AutoStuck", description = "Automatically enable stuck when you over void!", category = Category.MOVEMENT)
public class AutoStuck extends Module {
    FloatValue fallDistance = ValueBuilder.create(this, "Fall Distance").setDefaultFloatValue(10).setFloatStep(0.1f).setMinFloatValue(3f).setMaxFloatValue(15f).build().getFloatValue();
    TimeHelper timer = new TimeHelper();

    @EventTarget
    public void onMotion(EventMotion e) {
        if (!AutoClip.work && mc.thePlayer.ticksExisted > 20) {
            Stuck stuck = (Stuck) Naven.getInstance().getModuleManager().getModule(Stuck.class);

            if (stuck.isEnabled()) {
                timer.reset();
            }

            int pearlSlot = -1;

            // Get the pearl slot
            for (int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() == Items.ender_pearl) {
                    pearlSlot = i;
                    break;
                }
            }

            if (((pearlSlot != -1 && mc.thePlayer.fallDistance > fallDistance.getCurrentValue()) || (mc.thePlayer.posY + mc.thePlayer.motionY < -50))
                    && MoveUtils.isBoundingBoxOverVoid(mc.thePlayer.getEntityBoundingBox()) && !mc.thePlayer.onGround && timer.delay(1000)) {
                if (!stuck.isEnabled()) {
                    stuck.toggle();
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            timer.reset();
        }
    }
}
