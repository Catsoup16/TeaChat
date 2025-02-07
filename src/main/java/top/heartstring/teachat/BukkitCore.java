//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.heartstring.teachat.chat.ChatManager;
import top.heartstring.teachat.command.BukkitCommand;
import top.heartstring.teachat.config.Config;
import top.heartstring.teachat.dependency.DependencyManager;
import top.heartstring.teachat.network.BukkitNetworking;
import top.heartstring.teachat.utils.LoggerUtil;

public final class BukkitCore extends JavaPlugin {
    public static BukkitCore instance;

    public BukkitCore() {
    }

    public void onLoad() {
        try {
            Class.forName("net.kyori.adventure.text.Component");
            Class.forName("net.kyori.adventure.text.event.HoverEventSource");
            Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudiences");
        } catch (Error | Exception var2) {
            DependencyManager.init(this);
        }

    }

    public void onEnable() {
        LoggerUtil.init(this.getLogger());
        instance = this;
        long startTime = System.currentTimeMillis();
        LoggerUtil.log("§b插件正在加载....");
        LoggerUtil.log("§8服务端 " + String.format("%s %s", this.getServer().getName(), this.getServer().getVersion()));
        LoggerUtil.log("§8-------------------------------");
        PluginManager manager = Bukkit.getPluginManager();
        LoggerUtil.log("§7>加载监听模块");
        manager.registerEvents(new ChatManager(), this);
        LoggerUtil.log("§7>加载命令模块");
        BukkitCommand.init();
        LoggerUtil.log("§7>加载网络模块");
        BukkitNetworking.init(this);
        LoggerUtil.log("§7>加载配置模块");
        Config.load(this);
        LoggerUtil.log("§8-------------------------------");
        LoggerUtil.log(String.format("§b插件加载完成 %sms §8%s", System.currentTimeMillis() - startTime, this.getDescription().getVersion()));
    }

    public void onDisable() {
        super.onDisable();
        BukkitNetworking.close(this);
        ChatManager.unload$ChannelPermission();
    }
}
