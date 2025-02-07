//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.command;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.command.CommandSender;

public interface CommandAPI {
    boolean onConsole(CommandSender var1, String[] var2);

    boolean onPlayer(CommandSender var1, String[] var2);

    default List<String> onTabComplete(CommandSender commandSender, String[] strings) {
        return ImmutableList.of();
    }

    boolean hasPermission(CommandSender var1);
}
