package top.heartstring.teachat.utils;

import java.io.File;
import java.util.Map;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ConnectedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import top.heartstring.teachat.BungeeCore;
import top.heartstring.teachat.config.BCNode;

public class BungeeMessage extends Message implements BCNode {
    private static boolean isInit = false;

    public BungeeMessage(Plugin plugin) {
        this.registerNode();
        isInit = true;
        setRecord(new File(plugin.getDataFolder(), "logs"));
    }

    public static boolean isInit() {
        return isInit;
    }

    public static String addPrefix(String msg) {
        return "§7[§2Tea§8Chat§7] §f" + msg;
    }

    public static void log(String msg) {
        BungeeCore.instance.getLogger().info(msg);
    }

    public static boolean noConsole() {
        BungeeCore.instance.getLogger().warning("[TeaChat] 后台不能使用该命令");
        return true;
    }

    public static void msg(CommandSender sender, String msg) {
        if (sender instanceof ConnectedPlayer) {
            sender.sendMessage(new TextComponent(addPrefix(msg)));
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
        sender.sendMessage(new TextComponent(addPrefix("§c你没有权限执行这个命令")));
    }

    public void loadBus(Map<String, Configuration> configBus) {
        Configuration config = (Configuration)configBus.get("config");
        if (config != null && !config.getBoolean("record", false)) {
            setRecord((File)null);
            isInit = false;
            BCNode.Nodes.remove(this);
        }

    }
}
