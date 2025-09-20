package gal.yuxing.yuzusoft.murasame.naven.modules.impl.combat;

import gal.yuxing.yuzusoft.murasame.naven.events.impl.*;
import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.ui.cooldown.CooldownBar;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.Notification;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.NotificationLevel;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.*;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.skidonion.obfuscator.annotations.ControlFlowObfuscation;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

//@jnic.JNICInclude
@ControlFlowObfuscation
@NativeObfuscation(virtualize = NativeObfuscation.VirtualMachine.TIGER_BLACK)
@ModuleInfo(name = "AutoGapple", description = "Auto Eat Gapple When Attacking", category = Category.COMBAT)
public class AutoGapple extends Module {
    public final AtomicInteger c03s = new AtomicInteger(0);
    public static boolean velocityed = true;
    private final ConcurrentLinkedQueue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private static boolean eating = false;

    private CooldownBar currentBar;
    private volatile int currentMaxTicks = 0;

    FloatValue tick = ValueBuilder.create(this, "Eat Ticks").setDefaultFloatValue(33).setFloatStep(1).setMinFloatValue(1).setMaxFloatValue(40).build().getFloatValue();
    public static boolean eating() {
        return eating;
    }

    @Override
    public boolean onEnable() {
        if (mc.thePlayer == null) return false;
        if (mc.thePlayer.isDead) return false;
        if (mc.isSingleplayer()){
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.INFO, "You can't use this module in SinglePlayer !", 3000));
            this.setEnabled(false);
            return false;
        }
        packets.clear();
        c03s.set(0);
        eating = false;
        currentBar = null;
        currentMaxTicks = (int) tick.getCurrentValue();
        return false;
    }

    @Override
    public void onDisable() {
        if (currentBar != null) {
            currentBar.markExpired();
            currentBar = null;
        }
        eating = false;
        blink();
    }

    @EventTarget
    public void OnWorld(EventWorldUnload e){
        this.setEnabled(false);
    }

    @EventTarget
    public void onMoveMath(EventMotionCalculate event) {
        if (Minecraft.getMinecraft().thePlayer.positionUpdateTicks < 19 && !velocityed) {
            return;
        } else if (velocityed) {
            velocityed = false;
        }
    }

    public int finditem() {
        if (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null)
            return -100;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack();
            if (stack != null && stack.getItem() instanceof ItemAppleGold) {
                return i;
            }
        }
        return -100;
    }

    @EventTarget
    public void onTick(EventTick e) {
        mc.thePlayer.setSprinting(false);

        updateProgressBar();
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.POST) {
            packets.add(new C01PacketChatMessage("post"));
        }

        if (e.getType() == EventType.PRE) {
            if (mc.thePlayer == null || !mc.thePlayer.isEntityAlive()) {
                setEnabled(false);
                return;
            }

            int newMaxTicks = (int) tick.getCurrentValue();
            if (newMaxTicks != currentMaxTicks) {
                currentMaxTicks = newMaxTicks;
                if (currentBar != null) {
                    currentBar.setTitle("Eating: " + currentMaxTicks + " ticks");
                }
            }
        }

        if (finditem() == -100) {
            Naven.getInstance().getModuleManager().getModule("AutoGapple").setEnabled(false);
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.WARNING, "You don't have any gapple !", 3000));
            return;
        }

        eating = true;
        int currentC03s = c03s.get();
        if (currentC03s >= currentMaxTicks) {
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(finditem()));
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            blink();
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            c03s.set(0);
        } else if (mc.thePlayer.ticksExisted % 5 == 0) {
            while (!packets.isEmpty()) {
                Packet<?> packet = packets.poll();
                if (packet instanceof C01PacketChatMessage) {
                    break;
                }
                if (packet instanceof C03PacketPlayer) {
                    c03s.decrementAndGet();
                }
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
            }
        }

        if (eating) {
            mc.thePlayer.moveForward *= 0.2f;
            mc.thePlayer.moveStrafing *= 0.2f;
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        Packet<?> packet = e.getPacket();
        if (packet == null) return;
        if (e.getEventType() == EventPacket.EventState.SEND) {
            if (packet instanceof C00Handshake
                    || packet instanceof C00PacketLoginStart
                    || packet instanceof C00PacketServerQuery
                    || packet instanceof C01PacketPing
                    || packet instanceof C01PacketEncryptionResponse
                    || packet instanceof C01PacketChatMessage) {
                return;
            }

            if (packet instanceof C03PacketPlayer) {
                c03s.incrementAndGet();
            }
            if (packet instanceof C07PacketPlayerDigging || packet instanceof C09PacketHeldItemChange || packet instanceof C0EPacketClickWindow || packet instanceof C0DPacketCloseWindow) {
                e.setCancelled(true);
                return;
            }
            if (!(packet instanceof C08PacketPlayerBlockPlacement) && eating) {
                packets.add(packet);
                e.setCancelled(true);
            }
        }
    }

    private void blink() {
        if (mc.getNetHandler() == null || mc.thePlayer == null || mc.thePlayer.isDead)
            return;
        while (!packets.isEmpty()) {
            Packet<?> packet = packets.poll();
            if (packet instanceof C01PacketChatMessage || packet instanceof C07PacketPlayerDigging || packet instanceof C0EPacketClickWindow || packet instanceof C0DPacketCloseWindow)
                continue;
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
        }
        c03s.set(0);
    }

    private void releasepacket(){
        if (packets.isEmpty())
            return;
        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }

    private void updateProgressBar() {
        if (!eating) {
            if (currentBar != null) {
                currentBar.markExpired();
                currentBar = null;
            }
            return;
        }

        if (currentBar == null) {
            String title = "Eating ticks:";
            currentBar = new CooldownBar();
            currentBar.setTitle(title);
            Naven.getInstance().getCooldownBarManager().addBar(currentBar);
        }

        int currentC03s = c03s.get();
        int currentProgress = (int) ((float) currentC03s / currentMaxTicks * 100);
        currentBar.setProgress(currentProgress);
    }
}