package top.heartstring.teachat.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface BungeeCommandAPI {
    void onPlayer(ProxiedPlayer var1, String[] var2);

    void onConsole(CommandSender var1, String[] var2);
}
