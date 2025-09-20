package gal.yuxing.yuzusoft.murasame.naven.modules.impl.misc;
import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventMotion;
import gal.yuxing.yuzusoft.murasame.naven.modules.Category;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.modules.ModuleInfo;
import gal.yuxing.yuzusoft.murasame.naven.values.ValueBuilder;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.Notification;
import gal.yuxing.yuzusoft.murasame.naven.ui.notification.NotificationLevel;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
@ModuleInfo(name = "CaoYuan", description = "CaoYuan,CaoXinWei,CaoChengQin,LiangJin All died LOL", category = Category.MISC)
public class CaoYuan extends Module {

    private final BooleanValue CaoXinWei = ValueBuilder.create(this, "CaoXinWei").setDefaultBooleanValue(false).build().getBooleanValue();
    private final BooleanValue LiangJin = ValueBuilder.create(this, "LiangJin").setDefaultBooleanValue(false).build().getBooleanValue();
    private final BooleanValue CaoChengQin = ValueBuilder.create(this, "CaoChengQin").setDefaultBooleanValue(false).build().getBooleanValue();
    private final BooleanValue CaoYuan = ValueBuilder.create(this, "CaoYuan").setDefaultBooleanValue(false).build().getBooleanValue();
    private final BooleanValue Yuxing = ValueBuilder.create(this, "Yuxing").setDefaultBooleanValue(false).build().getBooleanValue();
    private final BooleanValue Manntilol =  ValueBuilder.create(this, "Manntilol").setDefaultBooleanValue(true).build().getBooleanValue();
    private final FloatValue Age = ValueBuilder.create(this, "曹源九族死光时间,单位毫秒").setDefaultFloatValue(0.1F).setFloatStep(0.1F).setMinFloatValue(0.1F).setMaxFloatValue(1).build().getFloatValue();
    @Override
    public boolean onEnable() {
        return false;
    }
    @EventTarget
    public void onUpdate(final EventMotion event) {
        if (CaoYuan.getCurrentValue()) {
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.ERROR, "CaoYuan Died!", 5000));
        }
        if (CaoXinWei.getCurrentValue()) {
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.ERROR, "CaoXinWei Died!", 5000));
        }
        if (LiangJin.getCurrentValue()) {
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.ERROR, "LiangJin Died!", 5000));
        }
        if  (CaoChengQin.getCurrentValue()) {
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.ERROR, "CaoChengQin Died!", 5000));
        }
        if  (CaoYuan.getCurrentValue()) {
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.ERROR, "CaoYuan Died!", 5000));
        }
        if  (Yuxing.getCurrentValue()) {
            Naven.getInstance().getNotificationManager().addNotification(new Notification(NotificationLevel.INFO,"CaoYuan,CaoXinWei,CaoChengQin,LiangJin All died LOL!",5000));
        }
        if (Manntilol.getCurrentValue()) {
            Naven.CLIENT_NAME = "Manntilol";
            Naven.CLIENT_DISPLAY_NAME = "Manntilol";
        }
    }

}
