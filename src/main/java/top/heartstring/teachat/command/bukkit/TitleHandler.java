package top.heartstring.teachat.command.bukkit;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import top.heartstring.teachat.command.CommandAPI;
import top.heartstring.teachat.network.BukkitNetworking;
import top.heartstring.teachat.network.packet.PlayerTitlePacket;
import top.heartstring.teachat.utils.BukkitMessage;

public class TitleHandler implements CommandAPI {
    private static final String Usage = "/teachat title <player> <&8title> <&2subTitle>";
    private static String PERM;

    public TitleHandler(String permission) {
        PERM = permission;
    }

    public boolean onConsole(CommandSender sender, String[] args) {
        if (args.length >= 4) {
            BukkitNetworking.sentTitle(sender, new PlayerTitlePacket(true, args[1].contains("all") ? null : args[1], (String)null, args[2], args[3]));
        } else {
            BukkitMessage.msg(sender, "/teachat title <player> <&8title> <&2subTitle>");
        }

        return true;
    }

    public boolean onPlayer(CommandSender sender, String[] args) {
        if (!sender.isOp() && !sender.hasPermission("teachat.title")) {
            BukkitMessage.noPermission(sender);
        } else if (args.length >= 4) {
            String arg1 = args[1];
            BukkitNetworking.sentTitle(sender, new PlayerTitlePacket(arg1.equalsIgnoreCase("all"), arg1, sender.getName(), args[2].replace('&', '§'), args[3].replace('&', '§')));
        } else {
            BukkitMessage.msg(sender, "/teachat title <player> <&8title> <&2subTitle>");
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        if (strings.length == 2) {
            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        } else if (strings.length == 3) {
            return ImmutableList.of("<title>");
        } else {
            return strings.length == 4 ? ImmutableList.of("<subTitle>") : CommandAPI.super.onTabComplete(commandSender, strings);
        }
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(PERM);
    }
}
