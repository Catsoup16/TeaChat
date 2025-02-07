package top.heartstring.teachat.command;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.heartstring.teachat.command.bukkit.ChannelHandler;
import top.heartstring.teachat.command.bukkit.ReloadHandler;
import top.heartstring.teachat.command.bukkit.TestHandler;
import top.heartstring.teachat.command.bukkit.TitleHandler;
import top.heartstring.teachat.utils.BukkitMessage;

public enum BukkitCommand {
    reload(new ReloadHandler("teachat.admin.reload")),
    channel(new ChannelHandler("teachat.channel")),
    title(new TitleHandler("teachat.title")),
    test(new TestHandler("teachat.test"));

    final CommandAPI command;

    public static void init() {
    }

    BukkitCommand(CommandAPI command) {
        this.command = command;
    }

    static {
        Handler command = new Handler();
        PluginCommand teachat = Bukkit.getPluginCommand("teachat");
        if (teachat != null) {
            teachat.setAliases(ImmutableList.of("chat"));
            teachat.setExecutor(command);
            teachat.setTabCompleter(command);
        }

    }

    static final class Handler implements TabCompleter, CommandExecutor {
        Handler() {
        }

        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String @NotNull [] args) {
            try {
                if (args.length == 0) {
                    BukkitMessage.msg(sender, "[reload]");
                    return true;
                } else {
                    CommandAPI handler = BukkitCommand.valueOf(args[0].toLowerCase()).command;
                    return sender instanceof Player ? handler.onPlayer(sender, args) : handler.onConsole(sender, args);
                }
            } catch (IllegalArgumentException var6) {
                BukkitMessage.msg(sender, String.format("§c子命令不存在§f:%s", args[0]));
                return true;
            } catch (NullPointerException e) {
                throw new RuntimeException(e);
            }
        }

        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
            try {
                return args.length == 1 ? Arrays.stream(BukkitCommand.values()).filter((b) -> b.command.hasPermission(sender)).map(Enum::name).filter((b) -> b.contains(args[0])).collect(Collectors.toList()) : BukkitCommand.valueOf(args[0].toLowerCase()).command.onTabComplete(sender, args);
            } catch (IllegalArgumentException var6) {
                return ImmutableList.of();
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
