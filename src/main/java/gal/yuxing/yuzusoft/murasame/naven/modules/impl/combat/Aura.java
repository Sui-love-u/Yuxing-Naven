package gal.yuxing.yuzusoft.murasame.naven.modules.impl.combat;

import com.viaversion.viarewind.protocol.protocol1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viarewind.utils.PacketUtil;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.fixes.AttackOrder;
import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.*;
import gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc.*;
import gal.yuxing.yuzusoft.murasame.naven.utils.*;
import gal.yuxing.yuzusoft.murasame.naven.utils.Managers.BlinkComponent;
import io.netty.buffer.Unpooled;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.modules.impl.move.Blink;
import gal.yuxing.yuzusoft.murasame.naven.modules.impl.move.Velocity;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.Notification;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.NotificationLevel;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.ModeValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gal.yuxing.yuzusoft.murasame.naven.events.api.types.Priority.HIGHEST;
@ModuleInfo(name = "KillAura", description = "Automatically attacks entities", category = Category.COMBAT)
public class Aura extends Module {
    private static final int targetColorRed = new Color(200, 0, 0, 60).getRGB();
    private static final int targetColorGreen = new Color(0, 200, 0, 60).getRGB();
    private static final int redColor = new Color(255, 0, 0, 255).getRGB();

    public static EntityLivingBase target, aimingTarget;
    public static TimeHelper disableHelper = new TimeHelper();
    public static List<EntityLivingBase> targets = new ArrayList<>();
    public static boolean blocked;
    public static boolean renderBlock;
    public float yaw;
    public float pitch;

    BooleanValue smart = ValueBuilder.create(this, "AI Aura").setDefaultBooleanValue(true).setOnUpdate((value) -> smartAura()).build().getBooleanValue();

    //    BooleanValue autoBlock = ValueBuilder.create(this, "Auto Block").setDefaultBooleanValue(true).setVisibility(() -> !smart.getCurrentValue()).build().getBooleanValue();
    ModeValue autoBlock = ValueBuilder.create(this, "Auto Block").setModes("GrimAC", "Hypixel", "None","Fake").build().getModeValue();
    BooleanValue targetEsp = ValueBuilder.create(this, "Target ESP").setDefaultBooleanValue(true).build().getBooleanValue();
    BooleanValue hitVecEsp = ValueBuilder.create(this, "Hit Vector ESP").setDefaultBooleanValue(true).build().getBooleanValue();
    BooleanValue attackPlayer = ValueBuilder.create(this, "Attack Player").setDefaultBooleanValue(true).build().getBooleanValue();
    BooleanValue attackInvisible = ValueBuilder.create(this, "Attack Invisible").setDefaultBooleanValue(false).build().getBooleanValue();
    BooleanValue attackAnimals = ValueBuilder.create(this, "Attack Animals").setDefaultBooleanValue(false).build().getBooleanValue();
    BooleanValue attackMobs = ValueBuilder.create(this, "Attack Mobs").setDefaultBooleanValue(false).build().getBooleanValue();
    BooleanValue lockView = ValueBuilder.create(this, "Lock View").setDefaultBooleanValue(false).setVisibility(() -> !smart.getCurrentValue()).build().getBooleanValue();
    BooleanValue throughBlock = ValueBuilder.create(this, "Through Block").setDefaultBooleanValue(true).setVisibility(() -> !smart.getCurrentValue()).build().getBooleanValue();
    BooleanValue aimThroughBlock = ValueBuilder.create(this, "Aim Through Block").setDefaultBooleanValue(true).setVisibility(() -> !smart.getCurrentValue()).build().getBooleanValue();
    BooleanValue breaker = ValueBuilder.create(this, "Armor Breaker").setDefaultBooleanValue(true).setVisibility(() -> !smart.getCurrentValue()).build().getBooleanValue();
    BooleanValue preferLegit = ValueBuilder.create(this, "Prefer Legit").setDefaultBooleanValue(false).build().getBooleanValue();
    BooleanValue preferNoArmor = ValueBuilder.create(this, "Prefer No Armor").setDefaultBooleanValue(false).build().getBooleanValue();
    BooleanValue hyp = ValueBuilder.create(this, "Hypixel").setDefaultBooleanValue(false).build().getBooleanValue();
    FloatValue range = ValueBuilder.create(this, "Attack Range").setDefaultFloatValue(3).setFloatStep(0.01f).setMinFloatValue(1f).setMaxFloatValue(6f).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();
    FloatValue blockRange = ValueBuilder.create(this, "Block Range").setDefaultFloatValue(5).setFloatStep(0.1f).setMinFloatValue(1f).setMaxFloatValue(6f).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();
    FloatValue aimRange = ValueBuilder.create(this, "Aim Range").setDefaultFloatValue(4).setFloatStep(0.1f).setMinFloatValue(1f).setMaxFloatValue(6f).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();
    FloatValue aps = ValueBuilder.create(this, "Attack Per Second").setDefaultFloatValue(10).setFloatStep(1f).setMinFloatValue(1f).setMaxFloatValue(20f).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();
    FloatValue switchSize = ValueBuilder.create(this, "Switch Size").setDefaultFloatValue(1).setFloatStep(1f).setMinFloatValue(1f).setMaxFloatValue(5f).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();
    FloatValue switchAttackTimes = ValueBuilder.create(this, "Switch Delay (Attack Times)").setDefaultFloatValue(1).setFloatStep(1).setMinFloatValue(1).setMaxFloatValue(10).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();
    FloatValue fov = ValueBuilder.create(this, "FoV").setDefaultFloatValue(360).setFloatStep(1).setMinFloatValue(10).setMaxFloatValue(360).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();
    FloatValue breakhealth = ValueBuilder.create(this, "ArmorBreaker Health").setDefaultFloatValue(10).setFloatStep(0.5f).setMinFloatValue(1f).setMaxFloatValue(20f).setVisibility(() -> !smart.getCurrentValue()).build().getFloatValue();

    ModeValue priority = ValueBuilder.create(this, "Priority").setModes("Health", "FoV", "Range", "None").setVisibility(() -> !smart.getCurrentValue()).build().getModeValue();
    ModeValue hitVectorMode = ValueBuilder.create(this, "Hit Vector Mode").setModes("Multi", "Player Eyes", "Custom").setVisibility(() -> !smart.getCurrentValue()).build().getModeValue();
    FloatValue hitVectorSize = ValueBuilder.create(this, "Hit Vector Height").setDefaultFloatValue(0).setFloatStep(0.01f).setMinFloatValue(0).setMaxFloatValue(2).setVisibility(() -> !smart.getCurrentValue() && hitVectorMode.isCurrentMode("Custom")).build().getFloatValue();

    ModeValue hitboxMode = ValueBuilder.create(this, "Hitbox Mode").setModes("Adaptive", "Custom").setVisibility(() -> !smart.getCurrentValue()).build().getModeValue();
    FloatValue hitboxSize = ValueBuilder.create(this, "Hitbox Size").setDefaultFloatValue(0).setFloatStep(0.01f).setMinFloatValue(-1).setMaxFloatValue(1).setVisibility(() -> !smart.getCurrentValue() && hitboxMode.isCurrentMode("Custom")).build().getFloatValue();
    private int lastSlot = -1;
    private int index;
    boolean working = false;

    public boolean blink;
    public boolean tick1;
    public boolean tick2;
    public boolean tick3;


    public void smartAura() {
        if (smart.getCurrentValue()) {
//            autoBlock.setCurrentValue(true);
            lockView.setCurrentValue(false);
            throughBlock.setCurrentValue(true);
            range.setCurrentValue(3f);
            aimRange.setCurrentValue(4f);
            blockRange.setCurrentValue(5f);
            aps.setCurrentValue(12);
            switchSize.setCurrentValue(1);
            switchAttackTimes.setCurrentValue(1);
            fov.setCurrentValue(360);
            aimThroughBlock.setCurrentValue(false);
            hitVectorMode.setCurrentValue(0);
            hitboxMode.setCurrentValue(0);
            priority.setCurrentValue(0);
            preferLegit.setCurrentValue(false);
        }
    }

    public float getHitbox() {
        if (hitboxMode.isCurrentMode("Adaptive")) {
            return mc.thePlayer.getCollisionBorderSize();
        } else {
            return hitboxSize.getCurrentValue();
        }
    }

    public float getHitVector() {
        if (hitVectorMode.isCurrentMode("Player Eyes")) {
            return mc.thePlayer.getEyeHeight();
        } else {
            return hitVectorSize.getCurrentValue();
        }
    }

    @EventTarget
    public void onRender(EventRender e) {
        if (targetEsp.getCurrentValue()) {
            for (EntityLivingBase entity : targets) {
                double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) e.renderPartialTicks - mc.getRenderManager().renderPosX;
                double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) e.renderPartialTicks - mc.getRenderManager().renderPosY;
                double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) e.renderPartialTicks - mc.getRenderManager().renderPosZ;

                Render3DUtils.drawSoiledEntityESP(posX, posY, posZ, (entity.width) / 2f + getHitbox(), entity.height, entity == target ? targetColorRed : targetColorGreen);
            }

            for (EntityLivingBase entity : targets) {
                Double[] position = Disabler.getNextTickPosition(entity);

                if (position != null) {
                    double posX = position[0] - mc.getRenderManager().renderPosX;
                    double posY = position[1] - mc.getRenderManager().renderPosY;
                    double posZ = position[2] - mc.getRenderManager().renderPosZ;

                    Render3DUtils.drawSoiledEntityESP(posX, posY, posZ, (entity.width) / 2f + getHitbox(), entity.height, entity == target ? targetColorRed : targetColorGreen);
                }
            }
        }

        if (hitVecEsp.getCurrentValue() && rotationData != null && lastRotationData != null && rotationData.getEye() != null && rotationData.getHitVec() != null && lastRotationData.getHitVec() != null && lastRotationData.getEye() != null) {
            double hitVecX = lastRotationData.getHitVec().xCoord + (rotationData.getHitVec().xCoord - lastRotationData.getHitVec().xCoord) * (double) e.renderPartialTicks;
            double hitVecY = lastRotationData.getHitVec().yCoord + (rotationData.getHitVec().yCoord - lastRotationData.getHitVec().yCoord) * (double) e.renderPartialTicks;
            double hitVecZ = lastRotationData.getHitVec().zCoord + (rotationData.getHitVec().zCoord - lastRotationData.getHitVec().zCoord) * (double) e.renderPartialTicks;
            Vec3 hitVec = new Vec3(hitVecX, hitVecY, hitVecZ);

            double eyeX = lastRotationData.getEye().xCoord + (rotationData.getEye().xCoord - lastRotationData.getEye().xCoord) * (double) e.renderPartialTicks;
            double eyeY = lastRotationData.getEye().yCoord + (rotationData.getEye().yCoord - lastRotationData.getEye().yCoord) * (double) e.renderPartialTicks;
            double eyeZ = lastRotationData.getEye().zCoord + (rotationData.getEye().zCoord - lastRotationData.getEye().zCoord) * (double) e.renderPartialTicks;
            Vec3 eye = new Vec3(eyeX, eyeY, eyeZ);

            double size = 0.025;
            Render3DUtils.drawESP(hitVec.addVector(-size, -size, -size), hitVec.addVector(size, size, size), redColor, true);

            if (mc.gameSettings.thirdPersonView != 0) {
                Render3DUtils.drawESP(eye.addVector(-size, -size, -size), eye.addVector(size, size, size), redColor, true);
            }

            double posY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * (double) e.renderPartialTicks;
            RenderUtils.drawLine(eye.yCoord - posY, hitVec, redColor);
        }
    }

    @Override
    public boolean onEnable() {
        if (this.range.getCurrentValue() > 3.0 && ServerUtils.serverType == ServerUtils.ServerType.GERM_PLUGIN) {
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.WARNING, "You are using a high range, may result ban!", 3000));
        }
//        BlinkComponent.dispatch();
        this.yaw = mc.thePlayer.rotationYaw;
        this.pitch = mc.thePlayer.rotationPitch;
        this.index = 0;
        target = null;
        tick1 = true;
        tick2 = false;
        tick3 = false;
        aimingTarget = null;
        targets.clear();
        smartAura();
        PotionResolver.resolve(1);
        return false;
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        Packet packet = e.getPacket();
        if (!e.isCancelled()) {
            if (e.getPacket() instanceof C09PacketHeldItemChange || e.getPacket() instanceof C07PacketPlayerDigging) {
                blocked = false;
            }
        }
        if (breaker.getCurrentValue() && packet instanceof C09PacketHeldItemChange && targets != null){
            int slot = ((C09PacketHeldItemChange) packet).getSlotId();
            if (slot == this.lastSlot && slot != -1) {
                e.setCancelled(true);
            }
            this.lastSlot = ((C09PacketHeldItemChange) packet).getSlotId();
        }
    }

    @EventTarget
    public void onAllPacket(EventGlobalPacket e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            target = aimingTarget = null;
            targets.clear();
        }
    }

    @Override
    public void onDisable() {
        this.yaw = mc.thePlayer.rotationYaw;
        this.pitch = mc.thePlayer.rotationPitch;

        if (blocked) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.gameSettings.keyBindUseItem.pressed = false;
            blocked = false;
        }
        renderBlock = false;
        BlinkComponent.dispatch();
        target = null;
        aimingTarget = null;

        super.onDisable();
    }

    @EventTarget
    public void onClick(EventClick e) {
        if (target == null) return;
        e.setCancelled(true);
    }

    @EventTarget
    public void onRespawn(EventRespawn event) {
        target = null;
        aimingTarget = null;

        toggle();
    }

    RotationUtils.Data lastRotationData, rotationData;

    int attackTimes = 0;
    float attacks = 0;

    @EventTarget(HIGHEST)
    public void onMotion(EventMotion event) {
        if (!disableHelper.delay(60)) {
            target = null;
            aimingTarget = null;
            rotationData = null;
            lastRotationData = null;
            targets.clear();
            return;
        }

        if (event.getType() == EventType.PRE) {


            if (blocked && autoBlock.isCurrentMode("GrimAC")) {
                if (ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_9)) {
                    if (Naven.getInstance().getModuleManager().getModule(Blink.class).isEnabled()) return;
                    PacketWrapper useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem.write(Type.VAR_INT, 1);
                    useItem.create(1);
                    PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                    PacketWrapper useItem2 = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem2.write(Type.VAR_INT, 0);
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C17PacketCustomPayload("束缚的郭明杰洗澡", new PacketBuffer(Unpooled.wrappedBuffer(new byte[]{1}))));
                    useItem2.getPacketType();
                    PacketUtil.sendToServer(useItem2, Protocol1_8To1_9.class, true, true);
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    blocked = true;
                }
            }
            if (targets == null || blocked){
                doUnBlock();
                blocked = false;
            }
            if (target == null && autoBlock.isCurrentMode("Hypixel")){
                doUnBlock();
                blocked = false;
            }
            if (!shouldBlock() && autoBlock.isCurrentMode("Hypixel")){
                renderBlock = false;
            }
            boolean isSwitch = switchSize.getCurrentValue() > 1;

            setSuffix(smart.getCurrentValue() ? "AI" : isSwitch ? "Switch" : "Single");
            updateAttackTargets();

            aimingTarget = shouldPreAim();

            lastRotationData = rotationData;
            rotationData = null;
            if (aimingTarget != null) {
                if (hitVectorMode.isCurrentMode("Multi")) {
                    rotationData = RotationUtils.getRotationDataToEntity(aimingTarget, getHitbox());
                } else {
                    rotationData = RotationUtils.getRotationDataToEntity(aimingTarget, getHitbox(), Collections.singletonList((double) getHitVector()));
                }

                if (rotationData.getRotation() != null) {
                    this.yaw = rotationData.getRotation().x;
                    this.pitch = rotationData.getRotation().y;
                }

                if (lockView.getCurrentValue()) {
                    mc.thePlayer.rotationYaw += RotationUtils.getAngleDifference(yaw, mc.thePlayer.rotationYaw);
                    mc.thePlayer.rotationPitch = pitch;
                }
            }

            if (targets.isEmpty()) {
                target = null;
                return;
            }

            if (this.index > targets.size() - 1) {
                this.index = 0;
            }

            if (targets.size() > 1 && (attackTimes >= switchAttackTimes.getCurrentValue() || (rotationData != null && rotationData.getDistance() > range.getCurrentValue()))) {
                attackTimes = 0;
                for (int i = 0; i < targets.size(); i++) {
                    this.index++;

                    if (this.index > targets.size() - 1) {
                        this.index = 0;
                    }

                    EntityLivingBase nextTarget = targets.get(index);
                    RotationUtils.Data data;

                    if (hitVectorMode.isCurrentMode("Multi")) {
                        data = RotationUtils.getRotationDataToEntity(nextTarget, getHitbox());
                    } else {
                        data = RotationUtils.getRotationDataToEntity(nextTarget, getHitbox(), Collections.singletonList((double) getHitVector()));
                    }

                    if (data.getDistance() < range.getCurrentValue()) {
                        break;
                    }
                }
            }

            if (this.index > targets.size() - 1 || !isSwitch) {
                this.index = 0;
            }

            target = targets.get(index);
            attacks += aps.getCurrentValue() / 20f;

            if (shouldBlock() && autoBlock.isCurrentMode("Hypixel")) {
                if (Naven.getInstance().getModuleManager().getModule(Blink.class).isEnabled()) return;
                preTickBlock();
                renderBlock = true;
                return;
            }
        } else {
            if (shouldBlock() && autoBlock.isCurrentMode("Hypixel")) {
//                if (Naven.getInstance().getModuleManager().getModule(Blink.class).isEnabled()) return;
//                        preTickBlock();
//                        renderBlock = true;
                return;
            }
            if (Disabler.disabled) {
                ItemStack itemInUse = mc.thePlayer.getItemInUse();
                if (itemInUse == null || !(itemInUse.getItem() instanceof ItemFood || itemInUse.getItem() instanceof ItemPotion)) {


                    if (Velocity.velocityPacket != null) {
                        this.doAttack();
                        attacks -= 1;
                    }

                    while (attacks >= 1 ) {
                        this.doAttack();
                        attacks -= 1;
                    }
                    if (shouldBlock()) {
                        if (autoBlock.isCurrentMode("GrimAC")) {
                            if (Naven.getInstance().getModuleManager().getModule(Blink.class).isEnabled()) return;
                            mc.getNetHandler().getNetworkManager().sendPacket(new CPacketPlayerTryUseItem(1));
                            blocked = true;
                        }
                        if (autoBlock.isCurrentMode("Fake")){
                            renderBlock = true;
                        }
                    }
                }

            }
        }

    }
    private final LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue<>();

    private boolean blocking;

//    private void releasePackets() {
//        while (!packets.isEmpty()) {
//            Packet<?> packet = packets.poll();
////            handleFakePlayerPacket(packet);
//            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
//        }
//    }

//    @EventTarget(Priority.LOW)
//    public void onPacketEEE(EventPacket e) {
//        if (e.getType() == EventType.SEND) {
//            final Packet<?> packet = e.getPacket();
//
//            if (EnumConnectionState.getFromPacket(packet) == EnumConnectionState.LOGIN) {
//                return;
//            }
//
//            if (EnumConnectionState.getDirection(packet) == EnumPacketDirection.CLIENTBOUND) {
//                return;
//            }
//
//            if (e.isCancelled()) {
//                return;
//            }
//
//            if (packet instanceof C01PacketChatMessage) {
//                return;
//            }
//
//            if (!blocking) return;
//
//            e.setCancelled(true);
//
//            packets.add(packet);
//        } else {
//            if (e.getPacket() instanceof S08PacketPlayerPosLook) {
    //                releasePackets();
//            }
//        }
//    }
//    boolean isSwapped;\

    /**
     *
     *
     */
    private void preTickBlock() {
        if (this.tick1) {
            BlinkComponent.blinking = true;
            if (blocking && !swapped) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("adk真可爱", new PacketBuffer(Unpooled.buffer())));
                blocking = false;
                swapped = true;
            }

            this.blink = true;
            tick1 = false;
            tick2 = true;
        } else if (tick2){
            if (swapped){
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                swapped = false;
            }
            tick2 = false;
            tick3 = true;
        } else if(tick3){
            doAttack();
            if(mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                MovingObjectPosition result = mc.objectMouseOver;
                if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && result.entityHit == target) {
                    if (!mc.playerController.isPlayerRightClickingOnEntity(mc.thePlayer, result.entityHit, result)) {
                        mc.playerController.interactWithEntitySendPacket(mc.thePlayer, result.entityHit);
                    }
                    if (!blocking) {
                        blocking = true;
                        mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                }
            }
            BlinkComponent.dispatch();
            this.blink = false;
            tick3 = false;
            tick1 = true;
        }
    }


    public void bestSword() {
        int bestSlot = 0;
        for (int i1 = 36; i1 < 45; i1++) {
            if (mc.thePlayer.inventoryContainer.inventorySlots.toArray()[i1] != null) {
                ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if (curSlot != null && InventoryManager.shouldSwapSword(curSlot)) {
                    bestSlot = i1 - 36;
                }
            }
        }

        mc.thePlayer.inventory.currentItem = bestSlot;
        mc.playerController.updateController();
    }

    private boolean hasSword() {
        if (mc.thePlayer.inventory.getCurrentItem() != null) {
            return mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
        } else {
            return false;
        }
    }

    private boolean swapped = false;
    private int serverSlot = -1;
    private void doUnBlock(){
        mc.gameSettings.keyBindUseItem.pressed = false;
        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        renderBlock = false;
        if (autoBlock.isCurrentMode("Hypixel")) {
            BlinkComponent.dispatch();
            blocking = false;
        }
    }

    public EntityLivingBase shouldPreAim() {
        EntityLivingBase target = Aura.target;

        if (target == null) {
            List<EntityLivingBase> aimTargets = getTargets(aimThroughBlock.getCurrentValue());
            if (!aimTargets.isEmpty()) {
                target = aimTargets.get(0);
            }
        }

        return target;
    }
    public boolean shouldBlock() {
        boolean hasTarget = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity).anyMatch(this::isValidBlock);

        return isEnabled() && hasTarget && hasSword() && !Naven.getInstance().getModuleManager().getModule(OffhandFeatures.class).isEnabled();
    }

    public void doAttack() {
        if (!targets.isEmpty() && rotationData != null && rotationData.getEye() != null) {
            for (EntityLivingBase target : targets) {
                float currentRange;
                double distance = RotationUtils.getMinDistance(target, getHitbox(), RotationManager.rotations);
                if (!mc.thePlayer.isSprinting()){
                    currentRange = 3;
                } else {
                    currentRange = range.getCurrentValue();
                }

                if (distance < currentRange) {
                    if (breaker.getCurrentValue() && mc.thePlayer.getHealth() > breakhealth.getCurrentValue()) {
                        working = false;
                        int axeSlot = findAxe();
                        if (axeSlot != -100 && !Naven.getInstance().getModuleManager().getModule(AutoGapple.class).isEnabled()) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(axeSlot));
                            mc.thePlayer.inventory.currentItem = axeSlot;
                            ChatUtils.addChatMessage("Switch Slot : " + axeSlot);
                        }
                        working = true;
                    }
                    attackEntity(target);
                    Naven.getInstance().getEventManager().call(new EventAttack(false));
                }
            }
        }
    }

    public void updateAttackTargets() {
        targets = this.getTargets(throughBlock.getCurrentValue());
    }

    public boolean isValidTarget(EntityLivingBase entity) {
        if (entity == mc.thePlayer) {
            return false;
        }

        if (entity.isDead || entity.getHealth() <= 0) {
            return false;
        }

        if (FriendManager.isFriend(entity)) return false;

        if (entity instanceof EntityOtherPlayerMP && ((EntityOtherPlayerMP) entity).isFakePlayer()) {
            return false;
        }

        if (AntiBot.isBot(entity)) return false;

        if (entity instanceof EntityArmorStand) return false;

        if (entity.isInvisible() && !attackInvisible.getCurrentValue()) return false;

        if (entity instanceof EntityPlayer && !attackPlayer.getCurrentValue())
            return false;

        if (entity instanceof EntityPlayer && (entity.width < 0.5 || entity.isPlayerSleeping())) return false;

        if ((entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityBat || entity instanceof EntityGolem) && !attackMobs.getCurrentValue())
            return false;

        if ((entity instanceof EntityAnimal || entity instanceof EntitySquid) && !attackAnimals.getCurrentValue())
            return false;

        if (entity instanceof EntityVillager && !attackAnimals.getCurrentValue()) return false;

        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator()) return false;

        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).getPlayerDeadTimer().delay(1000)) return false;

        return !Teams.isSameTeam(entity);
    }

    public boolean isValidAttack(EntityLivingBase entity) {
        if (!isValidTarget(entity)) {
            return false;
        }

        // Range check
        if (mc.thePlayer.getDistanceToEntity(entity) > aimRange.getCurrentValue()) {
            return false;
        }

        // Fov check
        return RotationUtils.inFoV(entity, fov.getCurrentValue());
    }

    public boolean isValidBlock(EntityLivingBase entity) {
        if (!isValidTarget(entity)) {
            return false;
        }

        // Range check
        return !(mc.thePlayer.getDistanceToEntity(entity) > blockRange.getCurrentValue());
    }

    public void attackEntity(EntityLivingBase entity) {
        attackTimes ++;

        if (Velocity.velocityPacket != null && !Velocity.velocityPacket.isModified() && !hyp.getCurrentValue()) {
            double x = Velocity.velocityPacket.getMotionX() / 8000D;
            double z = Velocity.velocityPacket.getMotionZ() / 8000D;

            boolean needSprint = !mc.thePlayer.serverSprintState;

            if (needSprint) {
                Velocity.velocityPacket.setToggleSprint(true);
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            }

            for (int i = 0; i < 8; i++) {
                AttackOrder.sendFixedAttack(mc.thePlayer, entity);
            }

            x *= Math.pow(0.6, 5);
            z *= Math.pow(0.6, 5);

            Velocity.velocityPacket.setMotionX((int) (x * 8000));
            Velocity.velocityPacket.setMotionZ((int) (z * 8000));
            Velocity.velocityPacket.setModified(true);

            if (needSprint) {
                Velocity.toggle = true;
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
        } else {
            AttackOrder.sendFixedAttack(mc.thePlayer, entity);
        }

        float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.thePlayer.inventory.getCurrentItem(), entity.getCreatureAttribute());

        if (sharpLevel > 0.0F) {
            mc.thePlayer.onEnchantmentCritical(entity);
        }
    }

    private static boolean hasArmor(EntityPlayer player) {
        for (int i = 1; i < 5; i++) {
            ItemStack stack = player.getEquipmentInSlot(i);
            if (stack != null) {
                return true;
            }
        }

        return false;
    }
    public static int findAxe() {
        if (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null) {
            return -100;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack();
            if (stack != null && stack.getItem() instanceof ItemAxe) {
                NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
                if (enchantmentTagList != null) {
                    for (int j = 0; j < enchantmentTagList.tagCount(); j++) {
                        NBTTagCompound nbt = enchantmentTagList.getCompoundTagAt(j);
                        if (nbt.hasKey("id") && nbt.hasKey("lvl") && nbt.getInteger("id") == 16) {
                            int level = nbt.getInteger("lvl");
                            if (level == 10) {
                                return i;
                            }
                        }
                    }
                }
            }
        }
        return -100;
    }
    public int findSword() {
        if (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null)
            return -100;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack();
            if (stack != null && stack.getItem() instanceof ItemSword) {
                return i;
            }
        }
        return -100;
    }
    private List<EntityLivingBase> getTargets(boolean throughBlock) {
        Stream<EntityLivingBase> stream = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .filter(this::isValidAttack)
                .filter(entity -> throughBlock || mc.thePlayer.canEntityBeSeen(entity));

        List<EntityLivingBase> possibleTargets = stream.collect(Collectors.toList());

        if (priority.isCurrentMode("Range")) {
            possibleTargets.sort(Comparator.comparingDouble(o -> o.getDistanceToEntity(mc.thePlayer)));
        } else if (priority.isCurrentMode("FoV")) {
            possibleTargets.sort(Comparator.comparingDouble(o -> RotationUtils.getDistanceBetweenAngles(RotationManager.rotations.x, RotationUtils.getRotations(o)[0])));
        } else if (priority.isCurrentMode("Health")) {
            possibleTargets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
        }

        if (preferLegit.getCurrentValue()) {
            possibleTargets.sort(Comparator.comparing(entityLivingBase -> entityLivingBase instanceof EntityPlayer && HackerDetector.isCheating(entityLivingBase.getName()) ? 1 : 0));
        }

        if (preferNoArmor.getCurrentValue()) {
            possibleTargets.sort(Comparator.comparing(entityLivingBase -> entityLivingBase instanceof EntityPlayer && hasArmor((EntityPlayer) entityLivingBase) ? 0 : 1));
        }

        return possibleTargets.subList(0, (int) Math.min(possibleTargets.size(), switchSize.getCurrentValue()));
    }
}