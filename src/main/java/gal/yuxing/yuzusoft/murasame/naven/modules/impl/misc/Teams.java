package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.api.types.EventType;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleInfo(name = "Teams", description = "Prevent attack teammates", category = Category.MISC)
public class Teams extends Module {
    public BooleanValue removeHitbox = ValueBuilder.create(this, "Remove Hitbox").setDefaultBooleanValue(false).build().getBooleanValue();

    private static String localPlayerTeam;

    public static boolean isSameTeam(Entity player) {
        if (!Naven.getInstance().getModuleManager().getModule(Teams.class).isEnabled()) {
            return false;
        }

        if (player instanceof EntityPlayer && localPlayerTeam != null) {
            return player.getDisplayName().getFormattedText().startsWith(localPlayerTeam);
        }

        return false;
    }

    public static boolean isSameTeam(String displayName) {
        if (!Naven.getInstance().getModuleManager().getModule(Teams.class).isEnabled()) {
            return false;
        }

        if (localPlayerTeam != null) {
            return displayName.startsWith(localPlayerTeam);
        }

        return false;
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        if (e.getType() == EventType.PRE) {
            String playerName = mc.thePlayer.getDisplayName().getFormattedText();
            localPlayerTeam = playerName.substring(0, 2);
        }
    }
}
