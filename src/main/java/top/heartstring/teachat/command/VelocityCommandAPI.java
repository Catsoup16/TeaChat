package top.heartstring.teachat.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface VelocityCommandAPI {
    void onPlayer(Player var1, String[] var2);

    void onConsole(ConsoleCommandSource var1, String[] var2);

    CompletableFuture<List<String>> suggestAsync(CommandSource var1, String[] var2);

    boolean hasPermission(CommandSource var1);
}
