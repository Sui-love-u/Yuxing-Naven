package gal.yuxing.yuzusoft.murasame.naven.commands.impl;

import dev.yalan.live.LiveClient;
import dev.yalan.live.netty.LiveProto;
import gal.yuxing.yuzusoft.murasame.naven.commands.Command;
import gal.yuxing.yuzusoft.murasame.naven.utils.ChatUtils;

public class CommandChat extends Command {
    public CommandChat() {
        super("i", "Post a message to LiveService", new String[0]);
    }

    @Override
    public void onCommand(String[] args) {
        if (args.length == 0) {
            ChatUtils.addChatMessage(".i <message>");
            return;
        }

        if (!LiveClient.INSTANCE.isActive()) {
            ChatUtils.addChatMessage("LiveClient is not active");
            return;
        }

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);

            if (i + 1 != args.length) {
                sb.append(' ');
            }
        }

        LiveClient.INSTANCE.sendPacket(LiveProto.createChat(sb.toString()));
    }

    @Override
    public String[] onTab(String[] args) {
        return new String[0];
    }
}
