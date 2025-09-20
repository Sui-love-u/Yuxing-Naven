package gal.yuxing.yuzusoft.murasame.naven.commands.impl;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.commands.Command;
import gal.yuxing.yuzusoft.murasame.naven.commands.CommandInfo;
import gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc.HackerDetector;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.Notification;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.NotificationLevel;
import net.minecraft.client.Minecraft;

@CommandInfo(name = "hacker", description = "Mark hackers.", aliases = {"hack"})
public class CommandHacker extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            String playerName = args[0];

            HackerDetector detector = (HackerDetector) Naven.getInstance().getModuleManager().getModule(HackerDetector.class);
            if (detector.addHacker(playerName)) {
                Notification notification = new Notification(NotificationLevel.INFO, playerName + " has been marked as a hacker!", 5000);
                Naven.getInstance().getNotificationManager().addNotification(notification);
            }
        }
    }

    @Override
    public String[] onTab(String[] args) {
        return Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream().map(info -> info.getGameProfile().getName()).filter(name -> name.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase())).toArray(String[]::new);
    }
}
