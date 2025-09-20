package gal.yuxing.yuzusoft.murasame.naven.ui.AltManager.NetEaseAltManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gal.yuxing.yuzusoft.murasame.naven.protocols.api.netease.NeteaseAccount;
import lombok.Getter;

import java.util.ArrayList;

public final class AltManager {
    public static AltManager Instance;
    @Getter
    private final ArrayList<NeteaseAccount> altList = new ArrayList();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AltManager() {
        Instance = this;
    }

    public void addAlt(NeteaseAccount alt) {
        this.altList.add(alt);
    }

}