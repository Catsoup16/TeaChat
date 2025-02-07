//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat;

import com.google.common.collect.ImmutableMap;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.yaml.snakeyaml.Yaml;
import top.heartstring.teachat.command.VelocityCommand;
import top.heartstring.teachat.dependency.VelocityDependencyManager;
import top.heartstring.teachat.network.VelocityNetworking;
import top.heartstring.teachat.utils.VelocityMessage;

@Plugin(
        id = "teachat",
        name = "TeaChat",
        version = "1.0.1"
)
public final class VelocityCore {
    public static VelocityCore instance;
    public final ProxyServer server;
    public final Logger logger;
    public final Path dataDirectory;
    private static final Map<String, String> CHANNEL_MAP = new ConcurrentHashMap<>();
    static Plugin annotation;

    @Inject
    public VelocityCore(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = (new File(dataDirectory.toAbsolutePath().getParent().toFile(), "TeaChat")).toPath();
        annotation = this.getClass().getAnnotation(Plugin.class);
        VelocityDependencyManager.init(this);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        VelocityMessage.log(" §b插件正在加载....");
        VelocityMessage.log(" §8代理端 " + String.format("%s %s", this.server.getVersion().getName(), this.server.getVersion().getVersion()));
        VelocityMessage.log(" §8-------------------------------");
        VelocityMessage.log(" §7>加载配置模块");
        VelocityCore.Config.init(this.dataDirectory, true);
        VelocityMessage.log(" §7>加载网络模块");
        this.server.getEventManager().register(this, new VelocityNetworking(this.server));
        VelocityMessage.log(" §7>加载命令模块");
        CommandManager commandManager = this.server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("teachatBC").aliases("chat").plugin(this).build(), new VelocityCommand.Handler());
        VelocityMessage.log(" §8-------------------------------");
        VelocityMessage.log(" §7已在 §bVelocity §7启动 §8version " + annotation.version());
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        Yaml yaml = new Yaml();
        Map<String, Map<String, String>> data = new HashMap<>();
        CHANNEL_MAP.keySet().forEach((n) -> data.put(n, ImmutableMap.of("channel", CHANNEL_MAP.get(n))));

        try {
            File playerDataFile = new File(this.dataDirectory.toFile(), "playerData.yml");
            if (!playerDataFile.exists() && !playerDataFile.createNewFile()) {
                throw new RuntimeException("Failed to create playerData file");
            } else {
                yaml.dump(data, new FileWriter(playerDataFile));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public static class Config {
        private static boolean isFirstTime = true;
        private static byte[] configData;

        public Config() {
        }

        public static int init(Path dataDirectory, boolean log) {
            File dataFolder = new File(dataDirectory.getParent().toFile(), VelocityCore.annotation.name());
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                throw new RuntimeException("Failed to create data folder");
            } else {
                File configFile = new File(dataFolder, "config.yml");
                File playerDataFile = new File(dataFolder, "playerData.yml");

                IOException e;
                try {
                    if (!configFile.exists()) {
                        InputStream is = VelocityCore.instance.getClass().getClassLoader().getResourceAsStream("proxy-config.yml");
                        if (is != null) {
                            Files.copy(is, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            is.close();
                            VelocityMessage.log(" > config.yml已创建");
                        }
                    }

                    Yaml config = new Yaml();
                    Map<String, Object> configMap = config.load(Files.newInputStream(configFile.toPath().toAbsolutePath()));
                    Boolean record = (Boolean)configMap.get("record");
                    VelocityMessage.setRecord(record != null ? VelocityCore.instance.dataDirectory.toAbsolutePath().toFile() : null);
                    if (!playerDataFile.exists()) {
                        InputStream is = VelocityCore.instance.getClass().getClassLoader().getResourceAsStream("playerData.yml");
                        if (is != null) {
                            Files.copy(is, playerDataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            is.close();
                            VelocityMessage.log(" > playerData.yml已创建");
                        }
                    }
                } catch (IOException var12) {
                    e = var12;
                    throw new RuntimeException(e);
                }

                try {
                    FileInputStream is = new FileInputStream(configFile);
                    int length = is.available();
                    byte[] data = new byte[length];
                    int finish = is.read(data, 0, length);
                    configData = data;
                    is.close();
                    if (log) {
                        VelocityMessage.log(" >config.yml读取大小: " + finish + "字节");
                    }

                    if (isFirstTime) {
                        isFirstTime = false;
                        Yaml yaml = new Yaml();
                        Map<String, Map<String, String>> playerData = yaml.load(new FileReader(playerDataFile));
                        if (playerData == null) {
                            playerData = new HashMap<>();
                        }

                        playerData.forEach((k, v) -> {
                            String channel = v.get("channel");
                            if (channel != null && !channel.isEmpty()) {
                                VelocityCore.CHANNEL_MAP.put(k, v.get("channel"));
                            } else {
                                VelocityCore.CHANNEL_MAP.put(k, "global");
                            }

                        });
                    }

                    return finish;
                } catch (IOException var11) {
                    e = var11;
                    throw new RuntimeException(e);
                }
            }
        }

        public static byte[] getConfig() {
            return configData;
        }
    }
}
