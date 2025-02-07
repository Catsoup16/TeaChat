package top.heartstring.teachat.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    public Config() {
    }
    @SuppressWarnings("all")
    public static void load(JavaPlugin plugin) {
        File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File configFile = new File(folder, "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        Map<String, ConfigurationSection> sections = new HashMap<>();
        if (config.getBoolean("publicConfig.enable", false)) {
            String path = config.getString("publicConfig.path", "");
            if (!path.isEmpty()) {
                File publicConfigFile = new File(path);
                if (!publicConfigFile.exists()) {
                    plugin.getLogger().log(Level.WARNING, "not found public config file:" + path);
                } else {
                    YamlConfiguration publicConfig = YamlConfiguration.loadConfiguration(publicConfigFile);
                    publicConfig.getKeys(false).forEach((s) -> {
                        sections.put(s, publicConfig.getConfigurationSection(s));
                    });
                }
            }
        }

        File channelFolder = new File(folder, "channel");
        if (!channelFolder.exists()) {
            plugin.saveResource("channel/global.yml", false);
        } else if (channelFolder.isFile()) {
            channelFolder.delete();
            plugin.saveResource("channel/global.yml", false);
        }

        File[] channelFiles = channelFolder.listFiles((dir, name) -> {
            return name.endsWith(".yml");
        });
        Map<String, Map<String, ConfigurationSection>> ChannelsSectionMap = new HashMap();
        if (channelFiles != null) {
            File[] var8 = channelFiles;
            int var9 = channelFiles.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                File channelFile = var8[var10];
                String channelName = channelFile.getName().replace(".yml", "");
                Map<String, ConfigurationSection> channelMap = new HashMap();
                YamlConfiguration channelConfig = YamlConfiguration.loadConfiguration(channelFile);
                channelConfig.getKeys(false).forEach((s) -> {
                    channelMap.put(s, channelConfig.getConfigurationSection(s));
                });
                ChannelsSectionMap.put(channelName, channelMap);
            }
        }

        config.getKeys(false).forEach((s) -> {
            sections.put(s, config.getConfigurationSection(s));
        });
        Node.Nodes.forEach((node) -> {
            node.sectionBus(sections);
            node.channelSectionBus(ChannelsSectionMap);
        });
    }
}
