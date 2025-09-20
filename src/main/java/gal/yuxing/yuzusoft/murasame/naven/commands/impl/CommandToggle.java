package gal.yuxing.yuzusoft.murasame.naven.commands.impl;

import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.commands.Command;
import gal.yuxing.yuzusoft.murasame.naven.commands.CommandInfo;
import gal.yuxing.yuzusoft.murasame.naven.exceptions.NoSuchModuleException;
import gal.yuxing.yuzusoft.murasame.naven.modules.Module;
import gal.yuxing.yuzusoft.murasame.naven.utils.ChatUtils;

@CommandInfo(name = "toggle", description = "Toggle a module", aliases = {"t"})
public class CommandToggle extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            String moduleName = args[0];
            try {
                Module module = Naven.getInstance().getModuleManager().getModule(moduleName);

                if (module != null) {
                    module.toggle();
                } else {
                    ChatUtils.addChatMessage("Invalid module.");
                }
            } catch (NoSuchModuleException e) {
                ChatUtils.addChatMessage("Invalid module.");
            }
        }
    }

    @Override
    public String[] onTab(String[] args) {
        return Naven.getInstance().getModuleManager().getModules().stream().map(Module::getName).filter(name -> name.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase())).toArray(String[]::new);
    }
}
