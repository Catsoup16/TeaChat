package top.heartstring.teachat.command.bukkit;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.heartstring.teachat.chat.ChatManager;
import top.heartstring.teachat.command.CommandAPI;
import top.heartstring.teachat.network.BukkitNetworking;
import top.heartstring.teachat.network.packet.PlayerOptionPacket;
import top.heartstring.teachat.utils.BukkitMessage;

public class ChannelHandler implements CommandAPI {
    public final String PERM;

    public ChannelHandler(String permission) {
        this.PERM = permission;
    }

    public boolean onConsole(CommandSender sender, String[] args) {
        return BukkitMessage.noConsole();
    }

    public boolean onPlayer(CommandSender sender, String[] args) {
        if (args.length >= 2 && sender instanceof Player) {
            String channel = args[1];
            if (ChatManager.hasChannel(channel)) {
                Player player = (Player)sender;
                if (ChatManager.hasPermission(player, channel)) {
                    PlayerOptionPacket packet = new PlayerOptionPacket(player.getName(), player.getUniqueId().toString());
                    packet.channel = channel;
                    BukkitNetworking.setOption(player, packet);
                } else {
                    BukkitMessage.msg(sender, "§c您没有权限接入这个频道 §e" + channel);
                }
            } else {
                BukkitMessage.msg(sender, "不存在这个频道 §c" + channel);
            }
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return ChatManager.getChannels();
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.PERM);
    }
}
