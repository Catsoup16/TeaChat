//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import top.heartstring.teachat.command.BungeeCommand;
import top.heartstring.teachat.config.BCNode;
import top.heartstring.teachat.config.ProxyConfig;
import top.heartstring.teachat.network.BungeeNetworking;
import top.heartstring.teachat.utils.BungeeMessage;

public final class BungeeCore extends Plugin implements BCNode {
    public static BungeeCore instance;
    private static final Map<String, String> CHANNEL_MAP = new ConcurrentHashMap<>();
    private static boolean playerDataLoaded = false;

    public BungeeCore() {
        this.registerNode();
    }

    public void onEnable() {
        instance = this;
        BungeeMessage.log(" §b插件正在加载....");
        BungeeMessage.log(" §8代理端 " + String.format("%s", this.getProxy().getVersion().replaceAll("git:", "")));
        BungeeMessage.log(" §8-------------------------------");
        BungeeMessage.log(" §7>加载网络模块");
        BungeeNetworking.init(this);
        BungeeMessage.log(" §7>加载命令模块");
        PluginManager manager = this.getProxy().getPluginManager();
        BungeeMessage.log(" §7>加载监听模块");
        manager.registerListener(this, new BungeeNetworking());
        BungeeMessage.log(" §7>加载命令模块");
        BungeeCommand.init();
        BungeeMessage.log(" §7>加载配置模块");
        ProxyConfig.load(this);
        BungeeMessage.log(" §8-------------------------------");
        BungeeMessage.log(String.format(" §7已在 §b%s §7启动 %s", this.getProxy().getName(), this.getDescription().getVersion()));
    }

    public ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    public void onDisable() {
        ProxyConfig.savePlayerData(this, CHANNEL_MAP);
    }

    public void loadBus(Map<String, Configuration> configBus) {
        if (!playerDataLoaded) {
            playerDataLoaded = true;
            Configuration playerData = configBus.get("playerData");
            playerData.getKeys().forEach((n) -> CHANNEL_MAP.put(n, playerData.getString(n + ".channel")));
        }

    }

    public static String getChannel(String name) {
        String channel = CHANNEL_MAP.get(name);
        if (channel == null) {
            CHANNEL_MAP.put(name, "global");
            return "global";
        } else {
            return channel;
        }
    }

    public static void setChannel(String name, String channel) {
        CHANNEL_MAP.put(name, channel);
    }
}
