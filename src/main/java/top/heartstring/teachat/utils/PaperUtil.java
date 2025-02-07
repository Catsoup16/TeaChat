package top.heartstring.teachat.utils;

import org.bukkit.Bukkit;

public class PaperUtil {
    private static final boolean isEnabled;

    public PaperUtil() {
    }

    public static boolean hasPaper() {
        return isEnabled;
    }

    static {
        String name = Bukkit.getServer().getName();
        isEnabled =
                name.contains("Paper") || name.contains("Purpur");
    }
}
