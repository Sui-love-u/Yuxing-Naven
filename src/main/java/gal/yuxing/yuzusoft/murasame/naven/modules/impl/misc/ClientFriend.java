package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.Notification;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.NotificationLevel;
import gal.yuxing.yuzusoft.murasame.naven.utils.TimeHelper;
import dev.yalan.live.LiveUser;
@ModuleInfo(name = "ClientFriend", description = "Treat other users as friend!", category = Category.MISC)
public class ClientFriend extends Module {
    public static TimeHelper attackTimer = new TimeHelper();
    public boolean onEnable(){
        return true;
    }
    @Override
    public void onDisable() {
        attackTimer.reset();
        Notification notification = new Notification(NotificationLevel.INFO, "You can attack other players after 15 seconds.", 15000);
        Naven.getInstance().getNotificationManager().addNotification(notification);
    }
}
