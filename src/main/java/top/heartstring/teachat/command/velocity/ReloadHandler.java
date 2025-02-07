package top.heartstring.teachat.command.velocity;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import top.heartstring.teachat.VelocityCore;
import top.heartstring.teachat.VelocityCore.Config;
import top.heartstring.teachat.command.VelocityCommandAPI;
import top.heartstring.teachat.utils.VelocityMessage;

public class ReloadHandler implements VelocityCommandAPI {
    private static String PERM;

    public ReloadHandler(String permission) {
        PERM = permission;
    }

    public void onPlayer(Player player, String[] args) {
        this.execute(player, args);
    }

    public void onConsole(ConsoleCommandSource source, String[] args) {
        this.execute(source, args);
    }

    private void execute(CommandSource source, String[] args) {
        if (this.hasPermission(source)) {
            VelocityMessage.log("配置已重载：" + Config.init(VelocityCore.instance.dataDirectory, false) + "字节");
        }

    }

    public CompletableFuture<List<String>> suggestAsync(CommandSource source, String[] args) {
        return CompletableFuture.completedFuture(ImmutableList.of());
    }

    public boolean hasPermission(CommandSource source) {
        return source instanceof Player ? source.hasPermission(PERM) : true;
    }
}
