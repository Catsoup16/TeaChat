package top.heartstring.teachat.command.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ConnectedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import top.heartstring.teachat.BungeeCore;
import top.heartstring.teachat.command.BungeeCommandAPI;
import top.heartstring.teachat.config.ProxyConfig;
import top.heartstring.teachat.utils.BungeeMessage;

public class ReloadHandler implements BungeeCommandAPI {
    public ReloadHandler() {
    }

    public void onPlayer(ProxiedPlayer player, String[] args) {
        this.executeCommand(player, args);
    }

    public void onConsole(CommandSender player, String[] args) {
        this.executeCommand(player, args);
    }

    private void executeCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("teachat.admin.reload")) {
            ProxyConfig.load(BungeeCore.instance);
            if (sender instanceof ConnectedPlayer) {
                BungeeMessage.msg(sender, "已重载");
            }

            BungeeCore.instance.getLogger().info("已重载");
        }

    }
}
