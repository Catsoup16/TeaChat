package top.heartstring.teachat.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import top.heartstring.teachat.BungeeCore;
import top.heartstring.teachat.command.bungee.ReloadHandler;

public enum BungeeCommand {
    reload(new ReloadHandler());

    final BungeeCommandAPI command;

    private BungeeCommand(BungeeCommandAPI command) {
        this.command = command;
    }

    public static void init() {
    }

    static {
        Handler handler = new Handler("teachatBC");
        BungeeCore instance = BungeeCore.instance;
        instance.getProxy().getPluginManager().registerCommand(instance, handler);
    }

    static final class Handler extends Command {
        public Handler(String name) {
            super(name);
        }

        public void execute(CommandSender sender, String[] args) {
            if (args.length == 0) {
                sender.sendMessage(new TextComponent("Usage: /teachat <command>"));
            } else {
                try {
                    BungeeCommand execute = BungeeCommand.valueOf(args[0].toLowerCase());
                    if (sender instanceof ProxiedPlayer) {
                        execute.command.onPlayer((ProxiedPlayer)sender, args);
                    } else {
                        execute.command.onConsole(sender, args);
                    }
                } catch (IllegalArgumentException var4) {
                    sender.sendMessage(new TextComponent(String.format("§c子命令不存在§f:%s", args[0])));
                }

            }
        }
    }
}
