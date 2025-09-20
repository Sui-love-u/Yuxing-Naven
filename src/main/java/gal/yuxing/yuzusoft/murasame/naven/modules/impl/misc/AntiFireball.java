package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import de.florianmichael.viamcp.fixes.AttackOrder;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import net.minecraft.entity.projectile.EntityFireball;

import java.util.Optional;

@ModuleInfo(name = "AntiFireball", description = "Prevents fireballs from damaging you", category = Category.MISC)
public class AntiFireball extends Module {
    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.getType() == EventType.PRE) {
            Optional<EntityFireball> fireball = mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityFireball && mc.thePlayer.getDistanceToEntity(entity) < 6).map(entity -> (EntityFireball) entity).findFirst();

            if (!fireball.isPresent()) {
                return;
            }

            EntityFireball entity = fireball.get();
            AttackOrder.sendFixedAttack(mc.thePlayer, entity);
        }
    }
}
