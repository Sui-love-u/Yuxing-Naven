package gal.yuxing.yuzusoft.murasame.naven.events;

import lombok.Data;
import gal.yuxing.yuzusoft.murasame.naven.events.api.events.Event;
import net.minecraft.entity.EntityLivingBase;

@Data
public class EventLivingUpdate implements Event {
    private final EntityLivingBase entity;
}
