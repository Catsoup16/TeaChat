package top.heartstring.teachat.command.bukkit;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.heartstring.teachat.command.CommandAPI;
import top.heartstring.teachat.utils.BukkitMessage;
import top.heartstring.teachat.utils.ColorUtil;

public class TestHandler implements CommandAPI {
    public final String PERMISSION;
    public TestHandler(String perm) {
        PERMISSION = perm;
    }
    public boolean onConsole(CommandSender sender, String[] args) {
        if (args.length >= 3){
            Player player = Bukkit.getPlayer(args[1]);
            if (player != null){
                player.sendMessage(ColorUtil.parseColor(PlaceholderAPI.setPlaceholders(player, args[2])));
                return true;
            }else {
                sender.sendMessage("Player not found");
            }
        }else {
            sender.sendMessage("Usage: /teachat test <player> <msg>");
        }
        return false;
    }

    public boolean onPlayer(CommandSender sender, String[] args) {
        return BukkitMessage.noPlayerCommand(sender);
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(PERMISSION);
    }
}
