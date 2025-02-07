//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.heartstring.teachat.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import top.heartstring.teachat.BungeeCore;
import top.heartstring.teachat.utils.BungeeMessage;

public class ProxyConfig {
    public ProxyConfig() {
    }

    public static void load(Plugin plugin) {
        File folder = plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Couldn't create folder " + folder.getAbsolutePath());
        } else {
            ConfigurationProvider provider = YamlConfiguration.getProvider(YamlConfiguration.class);
            File configFile = new File(folder, "config.yml");
            File playerDataFile = new File(folder, "playerData.yml");

            try {
                ClassLoader loader = BungeeCore.instance.getClassLoader();
                InputStream is;
                if (!configFile.exists() || !configFile.isFile()) {
                    is = loader.getResourceAsStream("proxy-config.yml");
                    Files.copy((InputStream)Objects.requireNonNull(is), configFile.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                    is.close();
                }

                if (!playerDataFile.exists() || !playerDataFile.isFile()) {
                    is = loader.getResourceAsStream("playerData.yml");
                    Files.copy((InputStream)Objects.requireNonNull(is), playerDataFile.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                    is.close();
                }

                Map<String, Configuration> configMap = new HashMap();
                configMap.put("config", provider.load(configFile));
                configMap.put("playerData", provider.load(playerDataFile));
                if (!BungeeMessage.isInit()) {
                    new BungeeMessage(plugin);
                }

                BCNode.Nodes.forEach((node) -> {
                    node.loadBus(configMap);
                });
            } catch (IOException var7) {
                IOException e = var7;
                throw new RuntimeException(e);
            }
        }
    }

    public static void savePlayerData(BungeeCore core, Map<String, String> playerData) {
        File folder = core.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new RuntimeException("Failed to create folder for " + folder.getAbsolutePath());
        } else {
            File playerDataFile = new File(folder, "playerData.yml");

            try {
                if (!playerDataFile.exists() || !playerDataFile.isFile()) {
                    ClassLoader loader = BungeeCore.instance.getClassLoader();
                    InputStream is = loader.getResourceAsStream("playerData.yml");
                    Files.copy((InputStream)Objects.requireNonNull(is), playerDataFile.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                    is.close();
                }

                ConfigurationProvider provider = YamlConfiguration.getProvider(YamlConfiguration.class);
                Configuration data = provider.load(playerDataFile);
                playerData.keySet().forEach((n) -> {
                    data.set(n + ".channel", playerData.get(n));
                });
                provider.save(data, playerDataFile);
            } catch (IOException var6) {
                IOException e = var6;
                throw new RuntimeException(e);
            }
        }
    }
}
