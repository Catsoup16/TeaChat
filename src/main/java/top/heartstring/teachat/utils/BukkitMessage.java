//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitMessage {
    public BukkitMessage() {
    }

    public static String addPrefix(String msg) {
        return "§7[§aTea§8Chat§7] §f" + msg;
    }

    public static void log(String msg) {
        LoggerUtil.log(msg);
    }

    public static boolean noConsole() {
        Bukkit.getLogger().warning("[TeaChat] 后台不能使用该命令");
        return true;
    }
    public static boolean noPlayerCommand(CommandSender sender){
        sender.sendMessage("[TeaChat] 后台不能使用该命令");
        return true;
    }

    public static void msg(CommandSender sender, String msg) {
        if (sender instanceof Player) {
            sender.sendMessage(addPrefix(msg));
        } else {
            log(msg);
        }

    }

    public static void msgIfNull(CommandSender sender, String msg, Object o) {
        if (o == null) {
            msg(sender, msg);
        }

    }

    public static void noPermission(CommandSender sender) {
        sender.sendMessage(addPrefix("§c你没有权限执行这个命令"));
    }
}
