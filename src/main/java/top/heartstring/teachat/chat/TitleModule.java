package top.heartstring.teachat.chat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.heartstring.teachat.network.packet.PlayerTitlePacket;
import top.heartstring.teachat.utils.BukkitMessage;

public class TitleModule {
    public TitleModule() {
    }

    public static boolean send(CommandSender sender, PlayerTitlePacket packet) {
        Player receiver = Bukkit.getPlayer(packet.player());
        if (receiver != null && receiver.isOnline()) {
            receiver.sendTitle(packet.title().replace('&', '§'), packet.subtitle().replace('&', '§'), 10, 20, 10);
            if (sender != null) {
                if (sender instanceof Player) {
                    BukkitMessage.msg(receiver, String.format("§e%s §a向你发送了屏幕标题", packet.sender()));
                } else {
                    BukkitMessage.msg(receiver, "§a服务器向您发送了屏幕标题");
                }

                BukkitMessage.msg(sender, String.format("§a你向 §e%s§a 发送了屏幕标题", packet.player()));
            }

            return true;
        } else {
            return false;
        }
    }
}
