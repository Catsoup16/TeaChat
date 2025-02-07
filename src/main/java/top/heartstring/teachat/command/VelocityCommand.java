package top.heartstring.teachat.command;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import top.heartstring.teachat.command.velocity.ReloadHandler;
import top.heartstring.teachat.utils.VelocityMessage;

public enum VelocityCommand {
    reload(new ReloadHandler("teachat.admin.reload"));

    final VelocityCommandAPI command;

    VelocityCommand(VelocityCommandAPI command) {
        this.command = command;
    }

    public static final class Handler implements SimpleCommand {
        public Handler() {
        }

        public void execute(SimpleCommand.Invocation invocation) {
            CommandSource source = invocation.source();
            String[] args = invocation.arguments();
            if (args.length == 0) {
                VelocityMessage.msg(source, "Usage: /teachat <command>");
            } else {
                try {
                    VelocityCommand handler = VelocityCommand.valueOf(args[0].toLowerCase());
                    if (source instanceof Player) {
                        handler.command.onPlayer((Player)source, args);
                    } else {
                        handler.command.onConsole((ConsoleCommandSource)source, args);
                    }
                } catch (IllegalStateException var5) {
                    VelocityMessage.msg(source, "Usage: /teachat <command>");
                }

            }
        }

        public CompletableFuture<List<String>> suggestAsync(SimpleCommand.Invocation invocation) {
            CommandSource source = invocation.source();
            String[] arguments = invocation.arguments();
            if (arguments.length == 1) {
                return CompletableFuture.completedFuture(Arrays.stream(VelocityCommand.values()).filter((v) -> v.command.hasPermission(source)).map(Enum::name).filter((s) -> s.contains(arguments[0])).collect(Collectors.toList()));
            } else {
                if (arguments.length >= 2) {
                    try {
                        return VelocityCommand.valueOf(arguments[0].toLowerCase()).command.suggestAsync(source, arguments);
                    } catch (IllegalStateException ignored) {
                    }
                }

                return CompletableFuture.completedFuture(ImmutableList.of());
            }
        }
    }
}
