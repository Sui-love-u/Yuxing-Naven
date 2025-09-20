package gal.yuxing.yuzusoft.murasame.naven.utils;

import lombok.Data;

@Data
public class PositionAndRotation {
    private final double x, y, z;
    private final float yaw, pitch;
}
