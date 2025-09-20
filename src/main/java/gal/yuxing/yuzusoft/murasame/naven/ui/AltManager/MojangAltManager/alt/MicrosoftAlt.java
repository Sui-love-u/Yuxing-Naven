package gal.yuxing.yuzusoft.murasame.naven.ui.AltManager.MojangAltManager.alt;


import gal.yuxing.yuzusoft.murasame.naven.ui.AltManager.MojangAltManager.AccountEnum;
import gal.yuxing.yuzusoft.murasame.naven.ui.AltManager.MojangAltManager.Alt;

public final class MicrosoftAlt extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName,String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
