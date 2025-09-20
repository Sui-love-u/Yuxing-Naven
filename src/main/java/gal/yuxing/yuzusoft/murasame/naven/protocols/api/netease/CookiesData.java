package gal.yuxing.yuzusoft.murasame.naven.protocols.api.netease;

import com.google.gson.annotations.SerializedName;
import gal.yuxing.yuzusoft.murasame.naven.protocols.api.netease.send.SAuth;
import gal.yuxing.yuzusoft.murasame.naven.protocols.api.utils.GsonIgnore;

import static gal.yuxing.yuzusoft.murasame.naven.protocols.api.NavenAPi.GSON;

public class CookiesData {
    @SerializedName("sauth_json")
    public String sauth_json;

    @GsonIgnore
    public SAuth sauth;

    @GsonIgnore
    public final String cookies;

    public CookiesData(String cookies) {
        this.cookies = cookies;
        CookiesData temp = GSON.fromJson(cookies, CookiesData.class);
        this.sauth_json = temp.sauth_json;
        this.sauth = new SAuth(this.sauth_json);
    }

    @Override
    public String toString() {
        return cookies;
    }
}
