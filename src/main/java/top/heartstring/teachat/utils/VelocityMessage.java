package top.heartstring.teachat.utils;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import java.io.BufferedWriter;
import java.time.format.DateTimeFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import top.heartstring.teachat.VelocityCore;

public class VelocityMessage extends Message {
    public static final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('§').build();
    public static final Component prefix = Component.text().content("[").color(TextColor.color(16777215)).append(Component.text("Tea").color(TextColor.color(5635925))).append(Component.text("Chat").color(TextColor.color(5592405))).append(Component.text("]").color(TextColor.color(16777215))).build();
    private static BufferedWriter WRITER;
    private static final Object LOCK = new Object();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public VelocityMessage() {
    }

    public static Component addPrefix(Component msg) {
        return prefix.append(msg);
    }

    public static void log(String msg) {
        VelocityCore.instance.server.getConsoleCommandSource().sendMessage(addPrefix(serializer.deserialize(msg)));
    }

    public static void log(Component msg) {
        VelocityCore.instance.server.getConsoleCommandSource().sendMessage(addPrefix(msg));
    }

    public static boolean noConsole() {
        VelocityCore.instance.server.getConsoleCommandSource().sendMessage(addPrefix(Component.text("后台不能使用该命令")));
        return true;
    }

    public static void msg(CommandSource source, String msg) {
        if (source instanceof Player) {
            source.sendMessage(addPrefix(Component.text(msg).color(TextColor.color(16777215))));
        } else {
            log(msg);
        }

    }

    public static void msgIfNull(CommandSource source, String msg, Object o) {
        if (o == null) {
            msg(source, msg);
        }

    }

    public static void noPermission(CommandSource source) {
        source.sendMessage(addPrefix(Component.text("你没有权限执行这个命令").color(TextColor.color(11156021))));
    }
}
