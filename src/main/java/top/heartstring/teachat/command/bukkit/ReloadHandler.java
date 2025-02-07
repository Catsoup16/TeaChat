//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.command.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.heartstring.teachat.BukkitCore;
import top.heartstring.teachat.command.CommandAPI;
import top.heartstring.teachat.config.Config;
import top.heartstring.teachat.utils.BukkitMessage;

public class ReloadHandler implements CommandAPI {
    private final String PERM;

    public ReloadHandler(String permission) {
        this.PERM = permission;
    }

    public boolean onConsole(CommandSender sender, String[] args) {
        return this.execute(sender, args);
    }

    public boolean onPlayer(CommandSender sender, String[] args) {
        return this.execute(sender, args);
    }

    private boolean execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(this.PERM)) {
            long time = System.currentTimeMillis();
            Config.load(BukkitCore.instance);
            BukkitMessage.log(String.format("已重载 §8 %sms", System.currentTimeMillis() - time));
            if (sender instanceof Player) {
                BukkitMessage.msg(sender, "已重载");
            }
        }

        return true;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.PERM);
    }

    static class ReloadInfo {
        ReloadInfo() {
        }
    }
}
