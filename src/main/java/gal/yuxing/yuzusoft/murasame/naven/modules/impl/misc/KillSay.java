package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import lombok.Getter;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventPacket;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventRespawn;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.utils.ServerUtils;
import gal.yuxing.yuzusoft.murasame.naven.values.Value;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.ModeValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@ModuleInfo(name = "KillSay", description = "Automatic send message when you killed someone!", category = Category.MISC)
public class KillSay extends Module {
    ModeValue prefix = ValueBuilder.create(this, "Prefix").setDefaultModeIndex(0).setModes("None", "@").build().getModeValue();

    @Getter
    private final List<BooleanValue> values = new ArrayList<>();

    Set<EntityPlayer> attackedPlayers = new CopyOnWriteArraySet<>();
    Random random = new Random();

    @EventTarget
    public void onRespawn(EventRespawn e) {
        attackedPlayers.clear();
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.POST && ServerUtils.serverType != ServerUtils.ServerType.LOYISA_TEST_SERVER) {
            for (EntityPlayer player : attackedPlayers) {
                Entity entityByID = mc.theWorld.getEntityByID(player.getEntityId());
                if (entityByID == null || mc.thePlayer.isInvisible()) {
                    attackedPlayers.remove(player);
                    continue;
                }

                if (player.isDead || player.getHealth() <= 0) {
                    String prefix = this.prefix.isCurrentMode("None") ? "" : this.prefix.getCurrentMode();

                    List<String> styles = values.stream().filter(BooleanValue::getCurrentValue).map(Value::getName).collect(Collectors.toList());
                    if (styles.isEmpty()) {
                        continue;
                    }

                    String style = styles.get(random.nextInt(styles.size()));
                    String message = prefix + String.format(style, player.getName());
                    mc.thePlayer.sendChatMessage(message);
                    attackedPlayers.remove(player);
                }
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (e.getType() == EventType.SEND) {
            if (e.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity) e.getPacket();
                if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    Entity entity = packet.getEntityFromWorld(mc.theWorld);
                    if (entity instanceof EntityPlayer) {
                        attackedPlayers.add((EntityPlayer) entity);
                    }
                }
            }
        }
    }
}
