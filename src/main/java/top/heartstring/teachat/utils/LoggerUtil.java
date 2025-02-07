package top.heartstring.teachat.utils;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class LoggerUtil {
    public static final String NAME = "§7[§aTea§8Chat§7]§f ";
    private static ConsoleCommandSender SENDER;
    private static Logger LOGGER;

    public LoggerUtil() {
    }

    public static void init(Logger logger) {
        LOGGER = logger;
        SENDER = Bukkit.getConsoleSender();
    }

    public static void log(String msg) {
        SENDER.sendMessage(ChatColor.translateAlternateColorCodes('&', "§7[§aTea§8Chat§7]§f " + msg));
    }

    public static void info(String msg) {
        SENDER.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
