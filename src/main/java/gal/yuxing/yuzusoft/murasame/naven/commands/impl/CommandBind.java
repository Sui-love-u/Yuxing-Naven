package gal.yuxing.yuzusoft.murasame.naven.commands.impl;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.commands.Command;
import gal.yuxing.yuzusoft.murasame.naven.commands.CommandInfo;
import gal.yuxing.yuzusoft.murasame.naven.events.api.EventTarget;
import gal.yuxing.yuzusoft.murasame.naven.events.impl.EventKey;
import gal.yuxing.yuzusoft.murasame.naven.exceptions.NoSuchModuleException;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.utils.ChatUtils;
import org.lwjgl.input.Keyboard;

@CommandInfo(name = "bind", description = "Bind a command to a key", aliases = {"b"})
public class CommandBind extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            String moduleName = args[0];
            try {
                Module module = Naven.getInstance().getModuleManager().getModule(moduleName);

                if (module != null) {
                    ChatUtils.addChatMessage("Press a key to bind " + moduleName + " to.");

                    Naven.getInstance().getEventManager().register(new Object() {
                        @EventTarget
                        public void onKey(EventKey e) {
                            if (e.isState()) {
                                module.setKey(e.getKey());
                                String keyName = Keyboard.getKeyName(e.getKey());

                                ChatUtils.addChatMessage("Bound " + moduleName + " to " + keyName + ".");
                                Naven.getInstance().getEventManager().unregister(this);

                                Naven.getInstance().getFileManager().save();
                            }
                        }
                    });
                } else {
                    ChatUtils.addChatMessage("Invalid module.");
                }
            } catch (NoSuchModuleException e) {
                ChatUtils.addChatMessage("Invalid module.");
            }
        } else if (args.length == 2) {
            String moduleName = args[0];
            String keyName = args[1];

            try {
                Module module = Naven.getInstance().getModuleManager().getModule(moduleName);

                if (module != null) {
                    if (keyName.equalsIgnoreCase("none")) {
                        module.setKey(Keyboard.KEY_NONE);
                        ChatUtils.addChatMessage("Unbound " + moduleName + ".");
                        Naven.getInstance().getFileManager().save();
                    } else {
                        int key = Keyboard.getKeyIndex(keyName.toUpperCase());

                        if (key != Keyboard.KEY_NONE) {
                            module.setKey(key);
                            ChatUtils.addChatMessage("Bound " + moduleName + " to " + keyName + ".");
                            Naven.getInstance().getFileManager().save();
                        } else {
                            ChatUtils.addChatMessage("Invalid key.");
                        }
                    }
                } else {
                    ChatUtils.addChatMessage("Invalid module.");
                }
            } catch (NoSuchModuleException e) {
                ChatUtils.addChatMessage("Invalid module.");
            }
        } else {
            ChatUtils.addChatMessage("Usage: .bind <module> [key]");
        }
    }

    @Override
    public String[] onTab(String[] args) {
        return Naven.getInstance().getModuleManager().getModules().stream().map(Module::getName).filter(name -> name.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase())).toArray(String[]::new);
    }
}
